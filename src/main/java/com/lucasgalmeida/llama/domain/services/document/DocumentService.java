package com.lucasgalmeida.llama.domain.services.document;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentService {
    String saveDocument(MultipartFile file) throws IOException;
    Resource getDocument(String fileName) throws IOException;
    void deleteDocument(String fileName);
    String getDocumentExtension(String fileName);
}
