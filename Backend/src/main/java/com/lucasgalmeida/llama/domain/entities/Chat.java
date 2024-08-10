package com.lucasgalmeida.llama.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String title;
    @OneToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_chat_user_id"))
    private User user;

    public Chat(String title, User user) {
        this.title = title;
        this.user = user;
    }
}