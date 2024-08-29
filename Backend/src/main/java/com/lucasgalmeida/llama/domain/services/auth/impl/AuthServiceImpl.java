package com.lucasgalmeida.llama.domain.services.auth.impl;

import com.lucasgalmeida.llama.domain.entities.User;
import com.lucasgalmeida.llama.domain.exceptions.auth.InvalidCredentialsException;
import com.lucasgalmeida.llama.domain.exceptions.auth.UserAlreadyExistsException;
import com.lucasgalmeida.llama.domain.exceptions.auth.UserNotFoundException;
import com.lucasgalmeida.llama.domain.services.auth.AuthService;
import com.lucasgalmeida.llama.application.dto.auth.AuthResponseDTO;
import com.lucasgalmeida.llama.application.dto.auth.LoginRequestDTO;
import com.lucasgalmeida.llama.application.dto.auth.RegisterRequestDTO;
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
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;


    @Override
    public AuthResponseDTO login(LoginRequestDTO body) {
        User user = userRepository.findByLogin(body.login()).orElseThrow(UserNotFoundException::new);
        if(passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = tokenService.generateToken(user);
            log.info("Sucesseful login for user: {}", body.login());
            return new AuthResponseDTO(user.getLogin(), token);
        }
        log.error("Wrong password for user: {}", body.login());
        throw new InvalidCredentialsException();
    }

    @Override
    public AuthResponseDTO register(RegisterRequestDTO body) {
        log.info("Trying register the user: {}", body.login());
        Optional<User> user = userRepository.findByLogin(body.login());

        if (user.isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User newUser = new User();
        newUser.setName(body.name());
        newUser.setPassword(passwordEncoder.encode(body.password()));
        newUser.setLogin(body.login());
        userRepository.save(newUser);

        String token = tokenService.generateToken(newUser);
        log.info("New user registered: {}", body.login());
        return new AuthResponseDTO(newUser.getName(), token);

    }

    @Override
    public User findAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new UsernameNotFoundException("Invalid user");
            }
            User user = (User) authentication.getPrincipal();
            if(Objects.isNull(user)) new UsernameNotFoundException("Invalid user");
            return user;
        } catch (Exception e) {
            throw new UsernameNotFoundException("Invalid user", e);
        }
    }

}
