package com.lucasgalmeida.llama.domain.services.vectorstore.impl;

import com.lucasgalmeida.llama.domain.entities.VectorStore;
import com.lucasgalmeida.llama.domain.repositories.VectorStoreRepository;
import com.lucasgalmeida.llama.domain.services.vectorstore.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreServiceImpl implements VectorStoreService {
    private final VectorStoreRepository repository;

    @Override
    public void deleteByIdList(List<UUID> vectorStoreIds) {
        repository.deleteAllById(vectorStoreIds);
        log.info("Deleting vectors by ids: %s".formatted(vectorStoreIds.stream().map(UUID::toString).collect(Collectors.joining(", "))));
    }

    @Override
    public Set<VectorStore> findByFileName(String fileName) {
        return repository.findByFileName(fileName);
    }
}
