package com.lucasgalmeida.llama.application.constants;

public enum ChatHistoryEnum {
    USER_REQUEST("USER_REQUEST"), IA_RESPONSE("IA_RESPONSE");

    private String descricao;

    ChatHistoryEnum(String descricao) {
        this.descricao = descricao;
    }
}
