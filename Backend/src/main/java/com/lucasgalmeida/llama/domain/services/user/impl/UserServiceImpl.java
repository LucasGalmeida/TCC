package com.lucasgalmeida.llama.domain.services.user.impl;

import com.lucasgalmeida.llama.domain.entities.User;
import com.lucasgalmeida.llama.domain.repositories.UserRepository;
import com.lucasgalmeida.llama.domain.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> findAll() {
        log.info("Buscando todos os usuários");
        return repository.findAll();
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        return repository.findByLogin(login);
    }

    @Override
    public User salvarUsuario(User user) {
        return repository.save(user);
    }
}
