package com.lucasgalmeida.llama.services.authservice.impl;

import com.lucasgalmeida.llama.domain.user.User;
import com.lucasgalmeida.llama.dto.auth.AuthResponseDTO;
import com.lucasgalmeida.llama.dto.auth.LoginRequestDTO;
import com.lucasgalmeida.llama.dto.auth.RegisterRequestDTO;
import com.lucasgalmeida.llama.infra.security.TokenService;
import com.lucasgalmeida.llama.repositories.UserRepository;
import com.lucasgalmeida.llama.services.authservice.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;


    @Override
    public AuthResponseDTO login(LoginRequestDTO body) {
        User user = repository.findByLogin(body.login()).orElseThrow(() -> new RuntimeException("User not found"));
        if(passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = tokenService.generateToken(user);
            return new AuthResponseDTO(user.getLogin(), token);
        }
        throw new RuntimeException("Error");
    }

    @Override
    public AuthResponseDTO register(RegisterRequestDTO body) {
        Optional<User> user = repository.findByLogin(body.login());

        if(user.isEmpty()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setLogin(body.login());
            this.repository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            return new AuthResponseDTO(newUser.getName(), token);
        }
        throw new RuntimeException("User already registred");
    }
}
