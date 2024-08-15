package com.lucasgalmeida.llama.domain.controllers;

import com.lucasgalmeida.llama.application.dto.auth.AuthResponseDTO;
import com.lucasgalmeida.llama.application.dto.auth.LoginRequestDTO;
import com.lucasgalmeida.llama.application.dto.auth.RegisterRequestDTO;
import com.lucasgalmeida.llama.domain.entities.User;
import com.lucasgalmeida.llama.domain.services.auth.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "auth-cotroller")
public class AuthController {
    private final AuthServiceImpl authServiceImpl;


    @Operation(summary = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Unautorized"),
            @ApiResponse(responseCode = "500", description = "Service internal error")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO body){
        return ResponseEntity.ok(authServiceImpl.login(body));
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO body){
        return ResponseEntity.ok(authServiceImpl.register(body));
    }

    @GetMapping("/authenticated")
    public ResponseEntity<User> getAuthenticatedUser(){
        return ResponseEntity.ok(authServiceImpl.findAuthenticatedUser());
    }
}
