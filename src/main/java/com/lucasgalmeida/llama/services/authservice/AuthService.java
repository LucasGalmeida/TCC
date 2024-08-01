package com.lucasgalmeida.llama.services.authservice;

import com.lucasgalmeida.llama.dto.auth.AuthResponseDTO;
import com.lucasgalmeida.llama.dto.auth.LoginRequestDTO;
import com.lucasgalmeida.llama.dto.auth.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO login(LoginRequestDTO body);
    AuthResponseDTO register(RegisterRequestDTO body);
}
