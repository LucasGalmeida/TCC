package com.lucasgalmeida.llama.domain.services.documentos.impl;

import com.lucasgalmeida.llama.domain.entities.Documentos;
import com.lucasgalmeida.llama.domain.entities.User;
import com.lucasgalmeida.llama.domain.entities.VectorStoreEntity;
import com.lucasgalmeida.llama.domain.exceptions.auth.UnauthorizedException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentNotFoundException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentStorageException;
import com.lucasgalmeida.llama.domain.exceptions.document.DocumentTypeException;
import com.lucasgalmeida.llama.domain.repositories.DocumentosRepository;
import com.lucasgalmeida.llama.domain.services.auth.AuthService;
import com.lucasgalmeida.llama.domain.services.documentos.DocumentosService;
import com.lucasgalmeida.llama.domain.services.vectorstore.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

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
public class DocumentosServiceImpl implements DocumentosService {
    private static final String[] SUPPORTED_CONTENT_TYPES = {"application/pdf"};
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final VectorStoreService vectorStoreService;
    private final AuthService authService;
    private final DocumentosRepository repository;
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
    public Path getFullPath(Documentos documentos) {
        return Paths.get(path, documentos.getUser().getId().toString(), documentos.getOriginalFileName());
    }

    @Override
    public List<Documentos> getMyDocuments() {
        User user = authService.findAuthenticatedUser();
        if (Objects.nonNull(user)) {
            return repository.findByUser_Id(user.getId());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Documentos> getAllProcessedDocuments() {
        return repository.findProcessedDocuments();
    }
    @Override
    public List<String> getFileNamesFromDocumentsIds(List<Integer> documentsIds) {
        List<Documentos> myDocumentos = repository.findAllById(documentsIds);
        return myDocumentos.stream().map(Documentos::getOriginalFileName).toList();
    }

    @Override
    public void salvarDocumento(Documentos documentos) {
        repository.save(documentos);
    }

    @Override
    @Transactional
    public Documentos updateDocument(Documentos documentos) {
        Documentos salvar = getDocumentById(documentos.getId());
        salvar.setName(documentos.getName());
        salvar.setDescription(documentos.getDescription());
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
    public Documentos saveDocumentByUser(MultipartFile file) throws FileAlreadyExistsException {
        // Cria um objeto do tipo documento
        Documentos documentos = new Documentos();
        if (repository.existsByName(file.getOriginalFilename())) {
            throw new FileAlreadyExistsException(file.getOriginalFilename());
        }
        documentos.setName(file.getOriginalFilename());
        documentos.setType(file.getContentType());
        LocalDateTime dateUpload = LocalDateTime.now();
        documentos.setDateUpload(dateUpload);
        // busca usuario que realizou a operacao
        documentos.setUser(authService.findAuthenticatedUser());
        try {
            // atualiza o nome do documento (concatena data de insercao para torna-lo unico)
            documentos.setOriginalFileName(saveDocument(file, documentos.getUser().getId(), dateUpload.format(DATE_TIME_FORMATTER)));
        } catch (IOException e) {
            throw new DocumentStorageException("Falha ao armazenar o arquivo: " + file.getOriginalFilename(), e);
        }
        return repository.save(documentos);
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
    public List<Documentos> saveDocumentsByUser(MultipartFile[] files) throws FileAlreadyExistsException {
        List<Documentos> retorno = new ArrayList<>();
        for (MultipartFile file : files) {
            retorno.add(saveDocumentByUser(file));
        }
        return retorno;
    }

    @Override
    public Documentos getDocumentById(Integer id) {
        Documentos documentos = repository.findById(id).orElseThrow(DocumentNotFoundException::new);
        User user = authService.findAuthenticatedUser();
        if (!user.getId().equals(documentos.getUser().getId())) {
            throw new UnauthorizedException("Usuario não autorizado a acessar esse documento");
        }
        return documentos;
    }

    @Override
    public Resource getResourceById(Integer id) {
        Documentos documentos = getDocumentById(id);
        Path fullPath = getFullPath(documentos);
        return new FileSystemResource(fullPath.toString());
    }

    @Override
    @Transactional
    public void deleteDocumentById(Integer id) {
        Documentos documentos = getDocumentById(id);
        if (documentos.isProcessed()) {
            List<UUID> vectorStoreIds = documentos.getVetores().stream().map(VectorStoreEntity::getId).toList();
            vectorStoreService.deleteByIdList(vectorStoreIds);
            documentos.getVetores().clear();
        }
        repository.deleteById(id);
        Path fullPath = getFullPath(documentos);
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
