package com.lucasgalmeida.llama.domain.services.document.impl;

import com.lucasgalmeida.llama.domain.exceptions.document.DocumentStorageException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentTypeException;
import com.lucasgalmeida.llama.domain.services.document.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

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

    @Override
    public String saveDocument(MultipartFile file) throws IOException {
        validateDocumentSize(file.getSize());
        validateDocumentType(file.getContentType());

        String newDocumentName = generateDocumentName(file.getOriginalFilename());
        Path fullPath = Paths.get(path, newDocumentName);
        try {
            file.transferTo(fullPath.toFile());
        } catch (IOException e) {
            throw new DocumentStorageException("Failed to store file: " + newDocumentName, e);
        }
        return newDocumentName;
    }

    @Override
    public Resource getDocument(String fileName) throws IOException {
        Path fullPath = Paths.get(path, fileName);
        Resource resource = new UrlResource(fullPath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new FileNotFoundException("Unable to read the file: " + fileName);
        }
    }

    @Override
    public void deleteDocument(String fileName) {
        try {
            Path fullPath = Paths.get(path, fileName);
            log.info("Attempting to delete file at path: {}", fullPath.toString());
            boolean isDeleted = Files.deleteIfExists(fullPath);
            if (isDeleted) {
                log.info("Document deleted successfully: {}", fullPath.toString());
            } else {
                throw new FileNotFoundException("Document not found or could not be deleted: %s".formatted(fullPath.toString()));
            }
        } catch (IOException e) {
            throw new DocumentStorageException("Failed to delete the file: " + fileName, e);
        }
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

    private String generateDocumentName(String originalDocumentName) {
        String extension = getDocumentExtension(originalDocumentName);
        String baseName = originalDocumentName.replace(extension, "");
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        return baseName + "_" + timestamp + extension;
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
