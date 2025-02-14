package com.lucasgalmeida.llama.domain.services.auth.impl;

import com.lucasgalmeida.llama.domain.entities.User;
import com.lucasgalmeida.llama.domain.exceptions.auth.InvalidCredentialsException;
import com.lucasgalmeida.llama.domain.exceptions.auth.UserAlreadyExistsException;
import com.lucasgalmeida.llama.domain.exceptions.auth.UserNotFoundException;
import com.lucasgalmeida.llama.domain.services.auth.AuthService;
import com.lucasgalmeida.llama.application.dto.auth.AuthResponseDTO;
import com.lucasgalmeida.llama.application.dto.auth.LoginRequestDTO;
import com.lucasgalmeida.llama.application.dto.auth.RegisterRequestDTO;
import com.lucasgalmeida.llama.domain.services.user.UserService;
import com.lucasgalmeida.llama.infra.security.TokenService;
import com.lucasgalmeida.llama.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public AuthResponseDTO login(LoginRequestDTO body) {
        User user = userService.findUserByLogin(body.login()).orElseThrow(UserNotFoundException::new);
        if(passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = tokenService.generateToken(user);
            log.info("Login bem sucedido para o usuário : {}", body.login());
            return new AuthResponseDTO(user.getLogin(), token);
        }
        log.error("Senha incorreta para o usuárior: {}", body.login());
        throw new InvalidCredentialsException();
    }

    @Override
    public AuthResponseDTO register(RegisterRequestDTO body) {
        log.info("Tentando registar o usuário: {}", body.login());
        Optional<User> user = userService.findUserByLogin(body.login());

        if (user.isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User newUser = new User();
        newUser.setName(body.name());
        newUser.setPassword(passwordEncoder.encode(body.password()));
        newUser.setLogin(body.login());
        userService.salvarUsuario(newUser);

        String token = tokenService.generateToken(newUser);
        log.info("Novo usuário registrado: {}", body.login());
        return new AuthResponseDTO(newUser.getName(), token);

    }

    @Override
    public User findAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new UsernameNotFoundException("Usuário inválido");
            }
            User user = (User) authentication.getPrincipal();
            if(Objects.isNull(user)) new UsernameNotFoundException("Usuário inválido");
            return user;
        } catch (Exception e) {
            throw new UsernameNotFoundException("Usuário inválido", e);
        }
    }

}
