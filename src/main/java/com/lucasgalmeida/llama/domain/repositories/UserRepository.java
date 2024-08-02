package com.lucasgalmeida.llama.domain.repositories;

import com.lucasgalmeida.llama.domain.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String login);


}
