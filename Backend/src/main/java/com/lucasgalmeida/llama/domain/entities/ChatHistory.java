package com.lucasgalmeida.llama.domain.entities;

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
    private String title;
    @NotNull
    private String message;
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_chat_history_user_id"))
    private User user;
}