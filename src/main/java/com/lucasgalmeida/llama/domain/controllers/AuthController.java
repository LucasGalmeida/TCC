package com.lucasgalmeida.llama.domain.controllers;

import com.lucasgalmeida.llama.application.dto.auth.AuthResponseDTO;
import com.lucasgalmeida.llama.application.dto.auth.LoginRequestDTO;
import com.lucasgalmeida.llama.application.dto.auth.RegisterRequestDTO;
import com.lucasgalmeida.llama.domain.services.auth.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl authServiceImpl;


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO body){
        return ResponseEntity.ok(authServiceImpl.login(body));
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO body){
        return ResponseEntity.ok(authServiceImpl.register(body));
    }
}
