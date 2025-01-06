package com.lucasgalmeida.llama.domain.repositories;

import com.lucasgalmeida.llama.domain.entities.Documentos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentosRepository extends JpaRepository<Documentos, Integer> {
    List<Documentos> findByUser_Id(Integer id);
    @Query(value = "FROM Documentos WHERE processed = true")
    List<Documentos> findProcessedDocuments();
    boolean existsByName(String name);


}
