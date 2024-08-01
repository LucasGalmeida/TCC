package com.lucasgalmeida.llama.controllers;

import com.lucasgalmeida.llama.dto.auth.AuthResponseDTO;
import com.lucasgalmeida.llama.dto.auth.LoginRequestDTO;
import com.lucasgalmeida.llama.dto.auth.RegisterRequestDTO;
import com.lucasgalmeida.llama.services.authservice.impl.AuthServiceImpl;
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
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO body){
        return ResponseEntity.ok(authServiceImpl.login(body));
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO body){
        return ResponseEntity.ok(authServiceImpl.register(body));
    }
}
