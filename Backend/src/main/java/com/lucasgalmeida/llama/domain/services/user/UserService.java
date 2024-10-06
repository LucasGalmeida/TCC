package com.lucasgalmeida.llama.domain.services.user;

import com.lucasgalmeida.llama.application.dto.auth.RegisterRequestDTO;
import com.lucasgalmeida.llama.domain.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findUserByLogin(String login);
    User salvarUsuario(User user);
}
