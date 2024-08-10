package com.lucasgalmeida.llama.domain.repositories;

import com.lucasgalmeida.llama.domain.entities.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Integer> {
}
