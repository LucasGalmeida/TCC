package com.lucasgalmeida.llama.domain.repositories;

import com.lucasgalmeida.llama.domain.entities.VectorStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;
import java.util.UUID;

public interface VectorStoreRepository extends JpaRepository<VectorStore, UUID> {
    @Query(value = "SELECT * FROM vector_store WHERE metadata->>'file_name' = :fileName or metadata->>'source' = :fileName", nativeQuery = true)
    Set<VectorStore> findByFileName(String fileName);
}
