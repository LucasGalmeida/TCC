package com.lucasgalmeida.llama.services;

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
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IAService {

    private final OllamaChatModel chatModel;
    private static final Logger log = LoggerFactory.getLogger(IAService.class);
    private final VectorStore vectorStore;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("classpath:/docs/PPC - CC - 2024.pdf")
    private Resource pdfResource;

    @Value("classpath:/prompts/prompt-padrao.st")
    private Resource promptOne;

    public String chat(String query) {
        PromptTemplate promptTemplate = new PromptTemplate(promptOne);
        Prompt prompt = promptTemplate.create(Map.of("input", query));
        String response = chatModel.call(prompt).getResult().getOutput().getContent();
        return response;
    }


    public void iniciaLeituraDeDocumentos() {
        Long count = (Long) entityManager.createNativeQuery("select count(*) from vector_store").getSingleResult();

        log.info("Contagem atual do vectorStore: {}", count);
        if (count == 0) {
            log.info("Carregando documentação do PDF no Vector Store");
            var config = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().withNumberOfBottomTextLinesToDelete(0)
                            .withNumberOfTopPagesToSkipBeforeDelete(0)
                            .build())
                    .withPagesPerDocument(1)
                    .build();

            var pdfReader = new PagePdfDocumentReader(pdfResource, config);
            var textSplitter = new TokenTextSplitter();
            List<Document> documents = pdfReader.get(); // Le o pdf
            List<Document> documentosProcessados = textSplitter.apply(documents); //Divide em chunks
            vectorStore.accept(documentosProcessados); // salva o embedding no vector store
            log.info("Aplicação esta pronta!");
        }
    }

}
