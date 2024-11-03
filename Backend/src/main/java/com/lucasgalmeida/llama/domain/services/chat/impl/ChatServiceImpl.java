package com.lucasgalmeida.llama.domain.services.chat.impl;

import com.lucasgalmeida.llama.application.constants.ChatHistoryEnum;
import com.lucasgalmeida.llama.domain.entities.Chat;
import com.lucasgalmeida.llama.domain.entities.ChatHistory;
import com.lucasgalmeida.llama.domain.entities.User;
import com.lucasgalmeida.llama.domain.exceptions.auth.UnauthorizedException;
import com.lucasgalmeida.llama.domain.exceptions.chat.ChatAlreadyExistsException;
import com.lucasgalmeida.llama.domain.exceptions.chat.ChatNotFoundException;
import com.lucasgalmeida.llama.domain.repositories.ChatHistoryRepository;
import com.lucasgalmeida.llama.domain.repositories.ChatRepository;
import com.lucasgalmeida.llama.domain.services.auth.AuthService;
import com.lucasgalmeida.llama.domain.services.chat.ChatService;
import com.lucasgalmeida.llama.domain.services.document.DocumentService;
import com.lucasgalmeida.llama.domain.services.vectorstore.VectorStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final DocumentService documentService;
    private final AuthService authService;
    private final VectorStore vectorStore;
    private final VectorStoreService vectorStoreService;
    private final ChatRepository chatRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private ChatClient chatClient;

    public ChatServiceImpl(ChatClient.Builder builder, DocumentService documentService, AuthService authService, VectorStore vectorStore, VectorStoreService vectorStoreService, ChatRepository chatRepository, ChatHistoryRepository chatHistoryRepository) {
        this.chatClient = builder
                .defaultSystem("Você é uma IA séria que consegue interagir com o usuário de maneira clara e objetiva. Se solicitado, forneça exemplos. NÃO consulte os documentos disponibilizados por contra própria. Você deve consultar a documentação APENAS se solicitado.")
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory())
                )
                .build();
        this.documentService = documentService;
        this.authService = authService;
        this.vectorStore = vectorStore;
        this.vectorStoreService = vectorStoreService;
        this.chatRepository = chatRepository;
        this.chatHistoryRepository = chatHistoryRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void preencherChatHistory() {
        List<Chat> chats = chatRepository.findAll();
        Map<String, List<Message>> memoria = new HashMap<>();
        chats.forEach(chat -> {
            List<Message> messageList = chatHistoryRepository.findByChat_IdOrderByDateAsc(chat.getId()).stream()
                    .map(mensagem -> {
                        ChatHistoryEnum messageType = mensagem.getType();
                        Message message =
                                messageType.equals(ChatHistoryEnum.USER_REQUEST) ?
                                        new UserMessage(mensagem.getMessage()) :
                                        new AssistantMessage(mensagem.getMessage());
                        return message;
                    }).toList();
            memoria.put(chat.getId().toString(), messageList);
        });
        InMemoryChatMemory memoriaSalva = new InMemoryChatMemory();
        memoria.forEach(memoriaSalva::add);
        chatClient = chatClient.mutate().defaultAdvisors(new MessageChatMemoryAdvisor(memoriaSalva)).build();
    }

    @Override
    public ChatHistory chatIA(String query, Integer chatId, List<Integer> documentsIds) {
        try {
            Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("Chat não encontrado com o id " + chatId));
            chatHistoryRepository.save(new ChatHistory(ChatHistoryEnum.USER_REQUEST, query, chat));

            List<String> fileNames = documentService.getFileNamesFromDocumentsIds(documentsIds);
            String response = "";
            if (CollectionUtils.isEmpty(fileNames)) {
                response = chatClient
                        .prompt().user(query)
                        .advisors((a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        )).call().content();
            } else {
                FilterExpressionBuilder b = new FilterExpressionBuilder();
                FilterExpressionBuilder.Op op = null;
                for (String fileName : fileNames) {
                    if (op == null) {
                        op = b.or(b.eq("file_name", fileName), b.eq("source", fileName));
                    } else {
                        op = b.or(op, b.or(b.eq("file_name", fileName), b.eq("source", fileName)));
                    }
                }
                response = chatClient
                        .prompt().user(query)
                        .advisors(a -> a
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        )
                        .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults().withFilterExpression(op != null ? op.build(): null)))
                        .call().content();
            }
            return chatHistoryRepository.save(new ChatHistory(ChatHistoryEnum.IA_RESPONSE, response, chat));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao se comunidar com a LLM");
        }
    }

    @Override
    public Flux<String> chatWithStream(String query, List<Integer> documentsIds) {
        try {
            List<String> fileNames = documentService.getFileNamesFromDocumentsIds(documentsIds);

            FilterExpressionBuilder b = new FilterExpressionBuilder();
            FilterExpressionBuilder.Op op = null;
            for (String fileName : fileNames) {
                if (op == null) {
                    op = b.or(b.eq("file_name", fileName), b.eq("source", fileName));
                } else {
                    op = b.or(op, b.or(b.eq("file_name", fileName), b.eq("source", fileName)));
                }
            }
            return chatClient
                    .prompt().user(query)
//                    .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults().withFilterExpression(op != null ? op.build(): null)))
                    .stream()
                    .content().map(content -> content.replace(" ", "\u00A0"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao se comunidar com a LLM");
        }
    }

    @Transactional
    @Override
    public void processDocumentById(Integer documentId) throws IOException {
        com.lucasgalmeida.llama.domain.entities.Document document = documentService.getDocumentById(documentId);
        if (document.isProcessed()) throw new RuntimeException("Documento ja processado");
        Path fullPath = documentService.getFullPath(document);
        Resource documentFile = documentService.getDocument(fullPath);
        if (!documentFile.exists()) throw new RuntimeException("Document nao encontrado");

        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(documentFile);
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();


        List<Document> documents = null;
        try {
            documents = tikaDocumentReader.get(); // Le o pdf
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Ocorreu um erro ao ler o PDF");
            throw e;
        }

        if (CollectionUtils.isEmpty(documents)) throw new RuntimeException("Não foi possível ler o PDF");

        List<Document> documentosProcessados = null;

        try {
            documentosProcessados = tokenTextSplitter.apply(documents);  //Divide em chunks
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Ocorreu um erro ao converter o PDF em chunks");
            throw e;
        }

        if (CollectionUtils.isEmpty(documentosProcessados))
            throw new RuntimeException("Não foi possível processar o PDF");

        vectorStore.accept(documentosProcessados);
        document.setProcessed(true);
        document.setVectorStores(findByFileName(document.getFileNameWithTimeStamp()));
        documentService.salvarDocumento(document);
    }

    @Override
    public Set<com.lucasgalmeida.llama.domain.entities.VectorStore> findByFileName(String fileName) {
        return vectorStoreService.findByFileName(fileName);
    }

    @Override
    public Chat createNewChat(String title) {
        User user = authService.findAuthenticatedUser();
        if (chatRepository.existsByUser_IdAndTitleIgnoreCase(user.getId(), title)) {
            throw new ChatAlreadyExistsException("Já existe um chat com esse nome!");
        }
        return chatRepository.save(new Chat(title, user));
    }

    @Override
    public Chat changeTitle(Integer id, String title) {
        User user = authService.findAuthenticatedUser();
        if (chatRepository.existsByUser_IdAndTitleIgnoreCase(user.getId(), title)) {
            throw new ChatAlreadyExistsException("Já existe um chat com esse nome!");
        }
        Chat chat = buscarPorId(id);
        chat.setTitle(title);
        return chatRepository.save(chat);
    }

    @Override
    public List<Chat> findAllChatsByUser() {
        User user = authService.findAuthenticatedUser();
        return chatRepository.findByUser_Id(user.getId());
    }

    @Override
    public List<ChatHistory> findChatHistoryByChatId(Integer id) {
        Chat chat = buscarPorId(id);
        User user = authService.findAuthenticatedUser();
        if (!user.getId().equals(chat.getUser().getId())) {
            throw new UnauthorizedException("Usuário não autorizado para ver esse chat");
        }
        return chatHistoryRepository.findByChat_IdOrderByDateAsc(id);
    }

    @Override
    @Transactional
    public void deleteChatById(Integer id) {
        Chat chat = buscarPorId(id);
        chatRepository.delete(chat);
    }

    @Override
    @Transactional
    public void deleteLastChatHistoryByChatId(Integer id) {

        chatHistoryRepository.deleteLastChatHistoryByChatId(id);
    }

    @Override
    public Chat buscarPorId(Integer id) {
        return chatRepository.findById(id).orElseThrow(() -> new ChatNotFoundException("Chat não encontrado"));
    }
}
