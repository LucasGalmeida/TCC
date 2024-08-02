package com.lucasgalmeida.llama.domain.services.user;

import com.lucasgalmeida.llama.domain.entities.user.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
}
