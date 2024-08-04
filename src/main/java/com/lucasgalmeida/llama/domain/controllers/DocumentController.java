package com.lucasgalmeida.llama.domain.controllers;

import com.lucasgalmeida.llama.domain.entities.Document;
import com.lucasgalmeida.llama.domain.services.document.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

//    @GetMapping(("/{name}")) // todo - id
//    public ResponseEntity<?> downalodFile(@PathVariable String name) {
//        Resource resource = null;
//        try {
//            resource = service.getDocument(name);
//        } catch (IOException e) {
//            return ResponseEntity.badRequest().body("Error when searching for file: " + e.getMessage());
//        }
//
//        String type = service.getDocumentExtension(name);
//
//        if (type.equalsIgnoreCase("pdf")) {
//            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(resource);
//        }
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }

    @PostMapping
    public ResponseEntity<Document> saveDocumentByUser(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(service.saveDocumentByUser(file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocumentById(@PathVariable Integer id) {
        service.deleteDocumentById(id);
        return ResponseEntity.ok("Document removed successfully!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getDocumentById(id));
    }
}

