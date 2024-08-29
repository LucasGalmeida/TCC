package com.lucasgalmeida.llama.domain.services.user.impl;

import com.lucasgalmeida.llama.domain.entities.User;
import com.lucasgalmeida.llama.domain.repositories.UserRepository;
import com.lucasgalmeida.llama.domain.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    @Override
    public List<User> findAll() {
        log.info("Searching all users");
        return repository.findAll();
    }
}
