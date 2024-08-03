package com.lucasgalmeida.llama.domain.controllers;

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
@RequestMapping("/file")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    @PostMapping
    public ResponseEntity<String> uploadArquivo(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = service.saveDocument(file);
            return ResponseEntity.ok(fileName);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error on uploading file: " + e.getMessage());
        }
    }

    @GetMapping(("/{name}"))
    public ResponseEntity<?> downloadArquivo(@PathVariable String name) {
        Resource resource = null;
        try {
            resource = service.getDocument(name);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error when searching for file: " + e.getMessage());
        }

        String type = service.getDocumentExtension(name);
        
        if (type.equalsIgnoreCase("pdf")) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(resource);
        }
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> removerArquivo(@PathVariable String name) {
        service.deleteDocument(name);
        return ResponseEntity.ok("Document removed successfully!");
    }

}

