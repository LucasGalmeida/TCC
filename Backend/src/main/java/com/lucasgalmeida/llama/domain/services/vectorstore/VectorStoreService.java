package com.lucasgalmeida.llama.domain.services.vectorstore;

import com.lucasgalmeida.llama.domain.entities.VectorStoreEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface VectorStoreService {
    void deleteByIdList(List<UUID> vectorStoreIds);
    Set<VectorStoreEntity> findByFileName(String fileName);
}
