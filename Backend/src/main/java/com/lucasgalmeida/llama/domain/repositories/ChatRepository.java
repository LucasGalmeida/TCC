package com.lucasgalmeida.llama.domain.repositories;

import com.lucasgalmeida.llama.domain.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findByUser_Id(Integer id);
}
