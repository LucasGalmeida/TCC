package com.lucasgalmeida.llama.domain.services.ia;

import com.lucasgalmeida.llama.domain.entities.VectorStore;

import java.io.IOException;
import java.util.Set;

public interface IAService {
    String chatGenerico(String query);
    String chatEspecifico(String query);
    void processDocumentById(Integer id) throws IOException;
    Set<VectorStore> findByFileName(String fileName);
}
