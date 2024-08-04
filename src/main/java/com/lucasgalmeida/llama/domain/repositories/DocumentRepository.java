package com.lucasgalmeida.llama.domain.repositories;

import com.lucasgalmeida.llama.domain.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
}
