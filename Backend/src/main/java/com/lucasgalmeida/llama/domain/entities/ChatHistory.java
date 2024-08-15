package com.lucasgalmeida.llama.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lucasgalmeida.llama.application.constants.ChatHistoryEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private ChatHistoryEnum type;
    @NotNull
    private LocalDateTime date;
    @NotNull
    private String message;
    @JsonBackReference("chat-history-chat")
    @ManyToOne
    @JoinColumn(name = "chat_id", foreignKey = @ForeignKey(name = "fk_chat_chat_history_id"))
    private Chat chat;

    public ChatHistory(ChatHistoryEnum type, String message, Chat chat) {
        this.type = type;
        this.message = message;
        this.date = LocalDateTime.now();
        this.chat = chat;
    }
}