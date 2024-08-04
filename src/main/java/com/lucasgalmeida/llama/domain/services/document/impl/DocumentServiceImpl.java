package com.lucasgalmeida.llama.domain.services.document.impl;

import com.lucasgalmeida.llama.domain.entities.Document;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentNotFoundException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentStorageException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentTypeException;
import com.lucasgalmeida.llama.domain.repositories.DocumentRepository;
import com.lucasgalmeida.llama.domain.services.auth.AuthService;
import com.lucasgalmeida.llama.domain.services.document.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    @Value("${filePath}")
    private String path;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxDocumentSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    private static final String[] SUPPORTED_CONTENT_TYPES = {"application/pdf"};
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final AuthService authService;
    private final DocumentRepository repository;

    @Override
    public String saveDocument(MultipartFile file, Integer userId, String dateUpload) throws IOException {
        validateDocumentSize(file.getSize());
        validateDocumentType(file.getContentType());

        String newDocumentName = generateDocumentName(file.getOriginalFilename(), dateUpload);
        Path directoryPath = Paths.get(path, userId.toString());

        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        Path fullPath = directoryPath.resolve(newDocumentName);
        try {
            file.transferTo(fullPath.toFile());
        } catch (IOException e) {
            throw new DocumentStorageException("Failed to store file: " + newDocumentName, e);
        }
        return newDocumentName;
    }

    @Override
    public Resource getDocument(Path fullPath) throws IOException {
        Resource resource = new UrlResource(fullPath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new FileNotFoundException("Unable to read the file");
        }
    }

    @Override
    public Path getFullPath(Document document){
        String fileNameWithoutExtension = removeExtension(document.getName());
        String extension = getExtension(document.getName());
        return Paths.get(path, document.getUser().getId().toString(), fileNameWithoutExtension + "_" + document.getDateUpload().format(DATE_TIME_FORMATTER) + "." + extension);
    }

    public void deleteDocument(Path fullPath) {
        try {
            log.info("Attempting to delete file at path: {}", fullPath);
            boolean isDeleted = Files.deleteIfExists(fullPath);
            if (isDeleted) {
                log.info("Document deleted successfully: {}", fullPath);
            } else {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            throw new DocumentStorageException("Document not found or could not be deleted");
        } catch (IOException e) {
            throw new DocumentStorageException("Failed to delete the file", e);
        }
    }

    private String removeExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex > filePath.lastIndexOf(File.separator)) {
            return filePath.substring(0, lastDotIndex);
        }
        return filePath;
    }

    private String getExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex > filePath.lastIndexOf(File.separator)) {
            return filePath.substring(lastDotIndex + 1);
        }
        return "";
    }

    private void validateDocumentSize(long fileSize) {
        int maxSize = parseSizeStringToInt(maxDocumentSize);
        int maxRequest = parseSizeStringToInt(maxRequestSize);

        if (fileSize > maxSize || fileSize > maxRequest) {
            throw new MaxUploadSizeExceededException(fileSize);
        }
    }

    private void validateDocumentType(String contentType) {
        if (contentType == null || !isSupportedContentType(contentType)) {
            throw new DocumentTypeException("Unsupported file type. Please upload ony PDFs.");
        }
    }

    private boolean isSupportedContentType(String contentType) {
        for (String supportedType : SUPPORTED_CONTENT_TYPES) {
            if (contentType.equals(supportedType) || contentType.startsWith(supportedType)) {
                return true;
            }
        }
        return false;
    }

    private String generateDocumentName(String originalDocumentName, String dateUpload) {
        String extension = getDocumentExtension(originalDocumentName);
        String baseName = originalDocumentName.replace(extension, "");
        return baseName + "_" + dateUpload + extension;
    }

    @Override
    public String getDocumentExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex).toLowerCase();
        } else {
            return "";
        }
    }

    @Override
    @Transactional
    public Document saveDocumentByUser(MultipartFile file) {
        Document document = new Document();
        document.setName(file.getOriginalFilename());
        document.setType(file.getContentType());
        LocalDateTime dataUpload = LocalDateTime.now();
        document.setDateUpload(dataUpload);
        document.setUser(authService.findAuthenticatedUser());
        document = repository.save(document);
        try {
            saveDocument(file, document.getUser().getId(), dataUpload.format(DATE_TIME_FORMATTER));
        } catch (IOException e){
            throw new DocumentStorageException("Failed to store file: " + file.getOriginalFilename(), e);
        }
        return document;
    }

    @Override
    public Document getDocumentById(Integer id){
        return repository.findById(id).orElseThrow(() ->
                new DocumentNotFoundException("Document not found")
        );
    }

    @Override
    @Transactional
    public void deleteDocumentById(Integer id) {
        Document document = getDocumentById(id);
        repository.deleteById(id);
        Path fullPath = getFullPath(document);
        deleteDocument(fullPath);
    }

    private int parseSizeStringToInt(String sizeString) {
        int multiplier = 1;
        if (sizeString.toLowerCase().endsWith("kb")) {
            multiplier = 1024;
        } else if (sizeString.toLowerCase().endsWith("mb")) {
            multiplier = 1024 * 1024;
        } else if (sizeString.toLowerCase().endsWith("gb")) {
            multiplier = 1024 * 1024 * 1024;
        }
        return Integer.parseInt(sizeString.replaceAll("\\D", "")) * multiplier;
    }
}
