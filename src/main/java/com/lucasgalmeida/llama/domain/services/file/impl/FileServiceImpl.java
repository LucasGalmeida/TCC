package com.lucasgalmeida.llama.domain.services.file.impl;

import com.lucasgalmeida.llama.domain.exceptions.file.FileStorageException;
import com.lucasgalmeida.llama.domain.exceptions.file.FileTypeException;
import com.lucasgalmeida.llama.domain.services.file.FileService;
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
public class FileServiceImpl implements FileService {

    @Value("${filePath}")
    private String path;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    private static final String[] SUPPORTED_CONTENT_TYPES = {"application/pdf"};
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public String saveFile(MultipartFile file) throws IOException {
        validateFileSize(file.getSize());
        validateFileType(file.getContentType());

        String newFileName = generateFileName(file.getOriginalFilename());
        Path fullPath = Paths.get(path, newFileName);
        try {
            file.transferTo(fullPath.toFile());
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + newFileName, e);
        }
        return newFileName;
    }

    @Override
    public Resource getFile(String fileName) throws IOException {
        Path fullPath = Paths.get(path, fileName);
        Resource resource = new UrlResource(fullPath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new FileNotFoundException("Unable to read the file: " + fileName);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            Path fullPath = Paths.get(path, fileName);
            Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete the file: " + fileName, e);
        }
    }

    private void validateFileSize(long fileSize) {
        int maxSize = parseSizeStringToInt(maxFileSize);
        int maxRequest = parseSizeStringToInt(maxRequestSize);

        if (fileSize > maxSize || fileSize > maxRequest) {
            throw new MaxUploadSizeExceededException(fileSize);
        }
    }

    private void validateFileType(String contentType) {
        if (contentType == null || !isSupportedContentType(contentType)) {
            throw new FileTypeException("Unsupported file type. Please upload ony PDFs.");
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

    private String generateFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String baseName = originalFileName.replace(extension, "");
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        return baseName + "_" + timestamp + extension;
    }

    @Override
    public String getFileExtension(String fileName) {
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
