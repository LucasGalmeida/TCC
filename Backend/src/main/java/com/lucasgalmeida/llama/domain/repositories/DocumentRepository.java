package com.lucasgalmeida.llama.domain.repositories;

import com.lucasgalmeida.llama.domain.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByUser_Id(Integer id);
    @Query(value = "FROM Document WHERE processed = true")
    List<Document> findProcessedDocuments();
    boolean existsByName(String name);


}
