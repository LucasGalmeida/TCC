package com.lucasgalmeida.llama.domain.services.document.impl;

import com.lucasgalmeida.llama.domain.entities.Document;
import com.lucasgalmeida.llama.domain.entities.User;
import com.lucasgalmeida.llama.domain.entities.VectorStore;
import com.lucasgalmeida.llama.domain.exceptions.auth.UnauthorizedException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentNotFoundException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentStorageException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentTypeException;
import com.lucasgalmeida.llama.domain.repositories.DocumentRepository;
import com.lucasgalmeida.llama.domain.services.auth.AuthService;
import com.lucasgalmeida.llama.domain.services.document.DocumentService;
import com.lucasgalmeida.llama.domain.services.vectorstore.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private static final String[] SUPPORTED_CONTENT_TYPES = {"application/pdf"};
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final VectorStoreService vectorStoreService;
    private final AuthService authService;
    private final DocumentRepository repository;
    @Value("${filePath}")
    private String path;
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxDocumentSize;
    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    @Override
    public Resource getDocument(Path fullPath) throws IOException {
        Resource resource = new UrlResource(fullPath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new FileNotFoundException("Falha ao ler documento");
        }
    }

    @Override
    public Path getFullPath(Document document) {
        return Paths.get(path, document.getUser().getId().toString(), document.getOriginalFileName());
    }

    @Override
    public List<Document> getMyDocuments() {
        User user = authService.findAuthenticatedUser();
        if (Objects.nonNull(user)) {
            return repository.findByUser_Id(user.getId());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Document> getAllProcessedDocuments() {
        return repository.findProcessedDocuments();
    }
    @Override
    public List<String> getFileNamesFromDocumentsIds(List<Integer> documentsIds) {
        List<Document> myDocuments = repository.findAllById(documentsIds);
        return myDocuments.stream().map(Document::getOriginalFileName).toList();
    }

    @Override
    public void salvarDocumento(Document document) {
        repository.save(document);
    }

    @Override
    @Transactional
    public Document updateDocument(Document document) {
        Document salvar = getDocumentById(document.getId());
        salvar.setName(document.getName());
        salvar.setDescription(document.getDescription());
        salvar = repository.save(salvar);
        return salvar;
    }

    public void deleteDocument(Path fullPath) {
        try {
            log.info("Tentando deletar o arquivo do caminho: {}", fullPath);
            boolean isDeleted = Files.deleteIfExists(fullPath);
            if (isDeleted) {
                log.info("Documento deletado com sucesso: {}", fullPath);
            } else {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            throw new DocumentStorageException("Documento não foi encontrado ou não pode ser deletado");
        } catch (IOException e) {
            throw new DocumentStorageException("Falha ao deletar o documento", e);
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
            throw new DocumentTypeException("Tipo de arquivo não suportado. Por favor insira apenas PDF's");
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


    @Override
    @Transactional
    public Document saveDocumentByUser(MultipartFile file) throws FileAlreadyExistsException {
        // Cria um objeto do tipo documento
        Document document = new Document();
        if (repository.existsByName(file.getOriginalFilename())) {
            throw new FileAlreadyExistsException(file.getOriginalFilename());
        }
        document.setName(file.getOriginalFilename());
        document.setType(file.getContentType());
        LocalDateTime dateUpload = LocalDateTime.now();
        document.setDateUpload(dateUpload);
        // busca usuario que realizou a operacao
        document.setUser(authService.findAuthenticatedUser());
        try {
            // atualiza o nome do documento (concatena data de insercao para torna-lo unico)
            document.setOriginalFileName(saveDocument(file, document.getUser().getId(), dateUpload.format(DATE_TIME_FORMATTER)));
        } catch (IOException e) {
            throw new DocumentStorageException("Falha ao armazenar o arquivo: " + file.getOriginalFilename(), e);
        }
        return repository.save(document);
    }

    private String saveDocument(MultipartFile file, Integer userId, String dateUpload) throws IOException {
        // verifica se o arquivo e muito grande
        validateDocumentSize(file.getSize());
        // verifica se o arquivo e pdf
        validateDocumentType(file.getContentType());

        if (ObjectUtils.isEmpty(file.getOriginalFilename())) {
            throw new DocumentTypeException("Nome do documento inválido");
        }

        // remove a extensao para pegar apenas o nome do arquivo
        String extension;
        int lastDotIndex = file.getOriginalFilename().lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < file.getOriginalFilename().length() - 1) {
            extension = file.getOriginalFilename().substring(lastDotIndex).toLowerCase();
        } else {
            extension = "";
        }
        String baseName = file.getOriginalFilename().replace(extension, "");
        String newDocumentName = baseName + "_" + dateUpload + ".pdf";

        // busca a pasta especifica do usuario no sistema
        Path directoryPath = Paths.get(path, userId.toString());

        // cria o arquivo na pasta definida pelo usuario
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // insere os dados do pdf no arquivo criado
        Path fullPath = directoryPath.resolve(newDocumentName);
        try {
            file.transferTo(fullPath.toFile());
        } catch (IOException e) {
            throw new DocumentStorageException("Falha ao armazenar documento: " + newDocumentName, e);
        }
        return newDocumentName;
    }

    @Override
    @Transactional
    public List<Document> saveDocumentsByUser(MultipartFile[] files) throws FileAlreadyExistsException {
        List<Document> retorno = new ArrayList<>();
        for (MultipartFile file : files) {
            retorno.add(saveDocumentByUser(file));
        }
        return retorno;
    }

    @Override
    public Document getDocumentById(Integer id) {
        Document document = repository.findById(id).orElseThrow(DocumentNotFoundException::new);
        User user = authService.findAuthenticatedUser();
        if (!user.getId().equals(document.getUser().getId())) {
            throw new UnauthorizedException("Usuario não autorizado a acessar esse documento");
        }
        return document;
    }

    @Override
    public Resource getResourceById(Integer id) {
        Document document = getDocumentById(id);
        Path fullPath = getFullPath(document);
        return new FileSystemResource(fullPath.toString());
    }

    @Override
    @Transactional
    public void deleteDocumentById(Integer id) {
        Document document = getDocumentById(id);
        if (document.isProcessed()) {
            List<UUID> vectorStoreIds = document.getVectorStores().stream().map(VectorStore::getId).toList();
            vectorStoreService.deleteByIdList(vectorStoreIds);
            document.getVectorStores().clear();
        }
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
