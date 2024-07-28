//package com.lucasgalmeida.llama.config;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.ai.reader.ExtractedTextFormatter;
//import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
//import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
//import org.springframework.ai.transformer.splitter.TokenTextSplitter;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.ai.document.Document;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class ReferenceDocsLoader {
//    private static final Logger log = LoggerFactory.getLogger(ReferenceDocsLoader.class);
//    private final VectorStore vectorStore;
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Value("classpath:/docs/PPC - CC - 2024.pdf")
//    private Resource pdfResource;
//
//    @PostConstruct
//    public void init() {
//        Long count = (Long) entityManager.createNativeQuery("select count(*) from vector_store").getSingleResult();
//
//        log.info("Contagem atual do vectorStore: {}", count);
//        if (count == 0) {
//            log.info("Carregando documentação do PDF no Vector Store");
//            var config = PdfDocumentReaderConfig.builder()
//                    .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().withNumberOfBottomTextLinesToDelete(0)
//                            .withNumberOfTopPagesToSkipBeforeDelete(0)
//                            .build())
//                    .withPagesPerDocument(1)
//                    .build();
//
//            var pdfReader = new PagePdfDocumentReader(pdfResource, config);
//            var textSplitter = new TokenTextSplitter();
//            List<Document> documents = pdfReader.get(); // Le o pdf
//            List<Document> documentosProcessados = textSplitter.apply(documents); //Divide em chunks
//            vectorStore.accept(documentosProcessados);
////            vectorStore.accept(textSplitter.apply(pdfReader.get()));
//            log.info("Aplicação esta pronta!");
//        }
//    }
//
//}
