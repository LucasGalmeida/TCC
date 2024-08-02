package com.lucasgalmeida.llama.application.dto.auth;


import jakarta.validation.constraints.NotEmpty;

public record RegisterRequestDTO(
        @NotEmpty(message = "O campo NOME é obrigatório") String name,
        @NotEmpty(message = "O campo LOGIN é obrigatório") String login,
        @NotEmpty(message = "O campo SENHA é obrigatório") String password
) {}
