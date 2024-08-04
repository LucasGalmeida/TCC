package com.lucasgalmeida.llama.domain.services.document;

import com.lucasgalmeida.llama.domain.entities.Document;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface DocumentService {
    String saveDocument(MultipartFile file, Integer id, String dateUpload) throws IOException;

    Resource getDocument(Path fullPath) throws IOException;

    String getDocumentExtension(String fileName);

    Document saveDocumentByUser(MultipartFile file);

    Document getDocumentById(Integer id);

    void deleteDocumentById(Integer id);

    Path getFullPath(Document document);
}
