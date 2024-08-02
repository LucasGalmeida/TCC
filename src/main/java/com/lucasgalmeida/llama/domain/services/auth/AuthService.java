package com.lucasgalmeida.llama.domain.services.auth;

import com.lucasgalmeida.llama.application.dto.auth.AuthResponseDTO;
import com.lucasgalmeida.llama.application.dto.auth.LoginRequestDTO;
import com.lucasgalmeida.llama.application.dto.auth.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO login(LoginRequestDTO body);
    AuthResponseDTO register(RegisterRequestDTO body);
}
