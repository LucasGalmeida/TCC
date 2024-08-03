package com.lucasgalmeida.llama.domain.controllers;

import com.lucasgalmeida.llama.domain.services.file.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FileController {

    private final FileService service;

    @PostMapping
    public ResponseEntity<String> uploadArquivo(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = service.saveFile(file);
            return ResponseEntity.ok(fileName);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error on uploading file: " + e.getMessage());
        }
    }

    @GetMapping(("/{name}"))
    public ResponseEntity<?> downloadArquivo(@PathVariable String name) {
        Resource resource = null;
        try {
            resource = service.getFile(name);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error when searching for file: " + e.getMessage());
        }

        String type = service.getFileExtension(name);
        
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
        service.deleteFile(name);
        return ResponseEntity.ok("File removed successfully!");
    }

}

