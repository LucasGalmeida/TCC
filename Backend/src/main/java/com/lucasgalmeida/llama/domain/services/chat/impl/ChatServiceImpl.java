package com.lucasgalmeida.llama.domain.services.chat.impl;

import com.lucasgalmeida.llama.application.constants.ChatHistoryEnum;
import com.lucasgalmeida.llama.domain.entities.Chat;
import com.lucasgalmeida.llama.domain.entities.ChatHistory;
import com.lucasgalmeida.llama.domain.entities.User;
import com.lucasgalmeida.llama.domain.exceptions.auth.UnauthorizedException;
import com.lucasgalmeida.llama.domain.exceptions.chat.ChatNotFoundException;
import com.lucasgalmeida.llama.domain.repositories.ChatHistoryRepository;
import com.lucasgalmeida.llama.domain.repositories.ChatRepository;
import com.lucasgalmeida.llama.domain.repositories.DocumentRepository;
import com.lucasgalmeida.llama.domain.repositories.VectorStoreRepository;
import com.lucasgalmeida.llama.domain.services.auth.AuthService;
import com.lucasgalmeida.llama.domain.services.document.DocumentService;
import com.lucasgalmeida.llama.domain.services.chat.ChatService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final OllamaChatModel chatModel;
    private final DocumentService documentService;
    private final AuthService authService;
    private final VectorStore vectorStore;
    private final VectorStoreRepository vectorStoreRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // todo - prompts dinamicos?
    @Value("classpath:/prompts/prompt-generico.st")
    private Resource promptGenerico;

    @Value("classpath:/prompts/prompt-especifico.st")
    private Resource promptEspecifico;

    @Value("classpath:/prompts/prompt-embedding.st")
    private Resource promptEmbedding;

    private final DocumentRepository documentRepository;
    private final ChatRepository chatRepository;
    private final ChatHistoryRepository chatHistoryRepository;

    @Override
    public String chatGenerico(String query) {
        PromptTemplate promptTemplate = new PromptTemplate(promptGenerico);
        Prompt prompt = promptTemplate.create(Map.of("input", query));
        String response = chatModel.call(prompt).getResult().getOutput().getContent();
        return response;
    }

    @Override
    public String chatEspecifico(String query) {
        PromptTemplate promptTemplate = new PromptTemplate(promptEspecifico);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", query);
        promptParameters.put("documents", String.join("\n", buscaDocumentosSemelhantes(query)));

        String response = chatModel.call(promptTemplate.create(promptParameters)).getResult().getOutput().getContent();
        return response;
    }

    @Override
    @Transactional
    public ChatHistory chatEmbedding(String query, Integer chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));
        chatHistoryRepository.save(new ChatHistory(ChatHistoryEnum.USER_REQUEST, query, chat));
        PromptTemplate promptTemplate = new PromptTemplate(promptEmbedding);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", query);
        promptParameters.put("documents", String.join("\n", buscaDocumentosSemelhantes(query)));
        String response = chatModel.call(promptTemplate.create(promptParameters)).getResult().getOutput().getContent();
        return chatHistoryRepository.save(new ChatHistory(ChatHistoryEnum.IA_RESPONSE, response, chat));
    }

    private List<String> buscaDocumentosSemelhantes(String message) {
        try {
            List<String> fileNames = documentService.getFileNamesFromAllDocuments();
            List<Document> documentosSemelhantes;
            if(fileNames.isEmpty()){
                documentosSemelhantes = new ArrayList<>();
            } else {
                FilterExpressionBuilder b = new FilterExpressionBuilder();
                FilterExpressionBuilder.Op op = null;
                for (String fileName : fileNames) {
                    if (op == null) {
                        op = b.or(b.eq("file_name", fileName), b.eq("source", fileName));
                    }
                    else {
                        op = b.or(op, b.or(b.eq("file_name", fileName), b.eq("source", fileName)));
                    }
                }
                if(Objects.nonNull(op)){
                    documentosSemelhantes = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(3)
                            .withFilterExpression(op.build())
                    );
                } else {
                    documentosSemelhantes = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(3));
                }
            }
            return documentosSemelhantes.stream().map(Document::getContent).toList();
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    @Override
    public void processDocumentById(Integer documentId) throws IOException {
        com.lucasgalmeida.llama.domain.entities.Document document = documentService.getDocumentById(documentId);
        if(document.isProcessed()) throw new RuntimeException("Document already processed");
        Path fullPath = documentService.getFullPath(document);
        Resource documentFile = documentService.getDocument(fullPath);
        if(!documentFile.exists()) throw new RuntimeException("Document not found");

        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(documentFile);
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();


        List<Document> documents = null;
        try {
            documents = tikaDocumentReader.get(); // Le o pdf
        } catch (Exception e){
            e.printStackTrace();
            log.error("Ocorreu um erro ao ler o PDF");
            throw e;
        }

        if(CollectionUtils.isEmpty(documents)) throw new RuntimeException("Não foi possível ler o PDF");

        List<Document> documentosProcessados = null;

        try {
            documentosProcessados = tokenTextSplitter.apply(documents);  //Divide em chunks
        } catch (Exception e){
            e.printStackTrace();
            log.error("Ocorreu um erro ao converter o PDF em chunks");
            throw e;
        }

        if(CollectionUtils.isEmpty(documentosProcessados)) throw new RuntimeException("Não foi possível processar o PDF");

        vectorStore.accept(documentosProcessados);
        document.setProcessed(true);
        document.setVectorStores(findByFileName(document.getFileNameWithTimeStamp()));
        documentRepository.save(document);
    }

    @Override
    public Set<com.lucasgalmeida.llama.domain.entities.VectorStore> findByFileName(String fileName) {
        return vectorStoreRepository.findByFileName(fileName);
    }

    @Override
    public Chat createNewChat(String title) {
        User user = authService.findAuthenticatedUser();
        return chatRepository.save(new Chat(title, user));
    }

    @Override
    public List<Chat> findAllChatsByUser() {
        User user = authService.findAuthenticatedUser();
        return chatRepository.findByUser_Id(user.getId());
    }

    @Override
    public List<ChatHistory> findChatHistoryByChatId(Integer id) {
        Chat chat = chatRepository.findById(id).orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        User user = authService.findAuthenticatedUser();
        if(!user.getId().equals(chat.getUser().getId())){
            throw new UnauthorizedException("User not authorized to access this document");
        }
        return chatHistoryRepository.findByChat_Id(id);
    }

    @Override
    @Transactional
    public void deleteChatById(Integer id) {
        Chat chat = chatRepository.findById(id).orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        chatRepository.delete(chat);
    }

}
