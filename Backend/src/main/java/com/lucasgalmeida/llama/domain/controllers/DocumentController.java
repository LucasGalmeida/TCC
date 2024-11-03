package com.lucasgalmeida.llama.domain.controllers;

import com.lucasgalmeida.llama.domain.entities.Document;
import com.lucasgalmeida.llama.domain.services.document.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;


@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    @PostMapping
    public ResponseEntity<Document> saveDocumentByUser(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(service.saveDocumentByUser(file));
    }

    @PostMapping("/documents")
    public ResponseEntity<List<Document>> saveDocumentsByUser(@RequestParam("files") MultipartFile[] files) throws IOException {
        return ResponseEntity.ok(service.saveDocumentsByUser(files));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocumentById(@PathVariable Integer id) {
        service.deleteDocumentById(id);
        return ResponseEntity.ok("Documento removido com sucesso!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getDocumentById(id));
    }

    @GetMapping("/resource/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable Integer id) throws IOException {
        Resource resource = service.getResourceById(id);
        if (resource.exists()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + resource.getFilename());
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/my-documents")
    public ResponseEntity<List<Document>> getMyDocuments() {
        return ResponseEntity.ok(service.getMyDocuments());
    }

    @PutMapping
    public ResponseEntity<Document> updateDocument(@RequestBody Document document) {
        return ResponseEntity.ok(service.updateDocument(document));
    }
}

