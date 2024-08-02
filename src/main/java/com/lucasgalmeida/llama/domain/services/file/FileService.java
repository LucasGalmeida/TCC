package com.lucasgalmeida.llama.domain.services.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String saveFile(MultipartFile file) throws IOException;
    Resource getFile(String fileName) throws IOException;
    void deleteFile(String fileName);
    String getFileExtension(String fileName);
}
