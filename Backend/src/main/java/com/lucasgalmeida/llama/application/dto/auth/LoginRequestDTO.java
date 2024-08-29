package com.lucasgalmeida.llama.application.dto.auth;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDTO(
        @NotEmpty(message = "O campo LOGIN é obrigatório") String login,
        @NotEmpty(message = "O campo SENHA é obrigatório") String password
) {}
