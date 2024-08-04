package com.lucasgalmeida.llama.domain.services.ia;

import java.io.IOException;

public interface IAService {
    String chatGenerico(String query);
    String chatEspecifico(String query);
    void processDocumentById(Integer id) throws IOException;
}
