package com.lucasgalmeida.llama.domain.controllers;

import com.lucasgalmeida.llama.domain.entities.Documentos;
import com.lucasgalmeida.llama.domain.services.documentos.DocumentosService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentosController {

    private final DocumentosService service;

    @PostMapping
    public ResponseEntity<Documentos> saveDocumentByUser(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(service.saveDocumentByUser(file));
    }

    @PostMapping("/documents")
    public ResponseEntity<List<Documentos>> saveDocumentsByUser(@RequestParam("files") MultipartFile[] files) throws IOException {
        return ResponseEntity.ok(service.saveDocumentsByUser(files));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocumentById(@PathVariable Integer id) {
        service.deleteDocumentById(id);
        return ResponseEntity.ok("Documento removido com sucesso!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Documentos> getDocumentById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getDocumentById(id));
    }

    @GetMapping("/resource/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable Integer id) {
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
    public ResponseEntity<List<Documentos>> getMyDocuments() {
        return ResponseEntity.ok(service.getMyDocuments());
    }

    @GetMapping
    public ResponseEntity<List<Documentos>> getAllProcessedDocuments() {
        return ResponseEntity.ok(service.getAllProcessedDocuments());
    }

    @PutMapping
    public ResponseEntity<Documentos> updateDocument(@RequestBody Documentos documentos) {
        return ResponseEntity.ok(service.updateDocument(documentos));
    }
}

