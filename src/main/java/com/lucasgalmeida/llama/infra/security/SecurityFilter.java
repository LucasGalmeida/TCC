package com.lucasgalmeida.llama.infra.security;

import com.lucasgalmeida.llama.domain.entities.user.User;
import com.lucasgalmeida.llama.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            var token = recoverToken(request);
            if (Objects.nonNull(token) && !token.isEmpty()) {
                var login = tokenService.validarToken(token);
                if (Objects.nonNull(login)) {
                    setUpSpringAuthentication(login);
                }
            }
        } catch (RuntimeException e) {
            log.error("Error on authenticate: ", e);
        }
        filterChain.doFilter(request, response);
    }

    private void setUpSpringAuthentication(String login) {
        User user = userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader(AUTH_HEADER);
        if (Objects.isNull(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            return null;
        }
        return authHeader.replace(TOKEN_PREFIX, "");
    }
}
