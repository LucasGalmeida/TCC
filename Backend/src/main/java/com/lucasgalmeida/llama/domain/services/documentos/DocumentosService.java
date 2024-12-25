package com.lucasgalmeida.llama.domain.services.documentos;

import com.lucasgalmeida.llama.domain.entities.Documentos;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.List;

public interface DocumentosService {

    Resource getDocument(Path fullPath) throws IOException;

    Documentos saveDocumentByUser(MultipartFile file) throws FileAlreadyExistsException;
    List<Documentos> saveDocumentsByUser(MultipartFile[] files) throws FileAlreadyExistsException;

    Documentos getDocumentById(Integer id);
    Resource getResourceById(Integer id);

    void deleteDocumentById(Integer id);

    Path getFullPath(Documentos documentos);
    List<Documentos> getMyDocuments();
    List<Documentos> getAllProcessedDocuments();
    List<String> getFileNamesFromDocumentsIds(List<Integer> documentsIds);
    void salvarDocumento(Documentos documentos);
    Documentos updateDocument(Documentos documentos);
}
