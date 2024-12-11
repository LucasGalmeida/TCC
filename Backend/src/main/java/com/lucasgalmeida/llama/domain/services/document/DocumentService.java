package com.lucasgalmeida.llama.domain.services.document;

import com.lucasgalmeida.llama.domain.entities.Document;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.List;

public interface DocumentService {

    Resource getDocument(Path fullPath) throws IOException;

    Document saveDocumentByUser(MultipartFile file) throws FileAlreadyExistsException;
    List<Document> saveDocumentsByUser(MultipartFile[] files) throws FileAlreadyExistsException;

    Document getDocumentById(Integer id);
    Resource getResourceById(Integer id);

    void deleteDocumentById(Integer id);

    Path getFullPath(Document document);
    List<Document> getMyDocuments();
    List<Document> getAllProcessedDocuments();
    List<String> getFileNamesFromDocumentsIds(List<Integer> documentsIds);
    void salvarDocumento(Document document);
    Document updateDocument(Document document);
}
