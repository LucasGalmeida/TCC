package com.lucasgalmeida.llama.application.dto;

import java.util.List;

public record RequestDTO(String query, List<Integer> documentsIds) {}