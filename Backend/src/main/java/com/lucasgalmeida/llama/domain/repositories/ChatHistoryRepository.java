package com.lucasgalmeida.llama.domain.repositories;

import com.lucasgalmeida.llama.domain.entities.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Integer> {
    List<ChatHistory> findByChat_IdOrderByDateAsc(Integer id);

    @Modifying
    @Query(value = "DELETE FROM chat_history WHERE id = ( " +
            "SELECT ch.id FROM chat_history ch WHERE ch.chat_id = :chatId " +
            "ORDER BY ch.date DESC LIMIT 1)", nativeQuery = true)
    void deleteLastChatHistoryByChatId(Integer chatId);
}
