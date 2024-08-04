package com.lucasgalmeida.llama.domain.services.ia.impl;

import com.lucasgalmeida.llama.domain.repositories.DocumentRepository;
import com.lucasgalmeida.llama.domain.services.document.DocumentService;
import com.lucasgalmeida.llama.domain.services.ia.IAService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IAServiceImpl implements IAService {

    private final OllamaChatModel chatModel;
    private final DocumentService documentService;
    private static final Logger log = LoggerFactory.getLogger(IAServiceImpl.class);
    private final VectorStore vectorStore;

    @PersistenceContext
    private EntityManager entityManager;
    @Value("classpath:/prompts/prompt-generico.st")
    private Resource promptOne;

    @Value("classpath:/prompts/prompt-especifico.st")
    private Resource promptTwo;
    private final DocumentRepository documentRepository;

    @Override
    public String chatGenerico(String query) {
        PromptTemplate promptTemplate = new PromptTemplate(promptOne);
        Prompt prompt = promptTemplate.create(Map.of("input", query));
        String response = chatModel.call(prompt).getResult().getOutput().getContent();
        return response;
    }

    @Override
    public String chatEspecifico(String query) {
        PromptTemplate promptTemplate = new PromptTemplate(promptTwo);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", query);
        promptParameters.put("documents", String.join("\n", buscaDocumentosSemelhantes(query)));

        String response = chatModel.call(promptTemplate.create(promptParameters)).getResult().getOutput().getContent();
        return response;
    }

    private List<String> buscaDocumentosSemelhantes(String message) {
        // todo - aqui busca na tabela inteira. Mas e se eu quiser buscar apenas as do usuário X?
        List<Document> documentosSemelhantes = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(3));
        return documentosSemelhantes.stream().map(Document::getContent).toList();
    }

    @Transactional
    @Override
    public void processDocumentById(Integer documentId) throws IOException {
        com.lucasgalmeida.llama.domain.entities.Document document = documentService.getDocumentById(documentId);
        if(document.isProcessed()) throw new RuntimeException("Document already processed");
        Path fullPath = documentService.getFullPath(document);
        Resource documentFile = documentService.getDocument(fullPath);
        if(!documentFile.exists()) throw new RuntimeException("Document not found");

        var config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().withNumberOfBottomTextLinesToDelete(0)
                        .withNumberOfTopPagesToSkipBeforeDelete(0)
                        .build())
                .withPagesPerDocument(1)
                .build();
        var pdfReader = new PagePdfDocumentReader(documentFile, config);
        var textSplitter = new TokenTextSplitter();

        List<Document> documents = pdfReader.get(); // Le o pdf
        List<Document> documentosProcessados = textSplitter.apply(documents); //Divide em chunks

        vectorStore.accept(documentosProcessados);
        document.setProcessed(true);
        documentRepository.save(document);
    }

}
