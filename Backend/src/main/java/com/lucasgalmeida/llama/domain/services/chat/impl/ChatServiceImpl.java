package com.lucasgalmeida.llama.domain.services.chat.impl;

import com.lucasgalmeida.llama.application.constants.ChatHistoryEnum;
import com.lucasgalmeida.llama.domain.entities.*;
import com.lucasgalmeida.llama.domain.exceptions.auth.UnauthorizedException;
import com.lucasgalmeida.llama.domain.exceptions.chat.ChatAlreadyExistsException;
import com.lucasgalmeida.llama.domain.exceptions.chat.ChatNotFoundException;
import com.lucasgalmeida.llama.domain.repositories.ChatHistoryRepository;
import com.lucasgalmeida.llama.domain.repositories.ChatRepository;
import com.lucasgalmeida.llama.domain.services.auth.AuthService;
import com.lucasgalmeida.llama.domain.services.chat.ChatService;
import com.lucasgalmeida.llama.domain.services.documentos.DocumentosService;
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
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
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

    private final DocumentosService documentosService;
    private final AuthService authService;
    private final VectorStore vectorStore;
    private final VectorStoreService vectorStoreService;
    private final ChatRepository chatRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private ChatClient chatClient;

    public ChatServiceImpl(ChatClient.Builder builder, DocumentosService documentosService, AuthService authService, VectorStore vectorStore, VectorStoreService vectorStoreService, ChatRepository chatRepository, ChatHistoryRepository chatHistoryRepository) {
        this.chatClient = builder
                .defaultSystem("Responda sempre da maneira mais sucinta possível. Se não souber a resposta, apenas diga que não sabe responser.")
                .build();
        this.documentosService = documentosService;
        this.authService = authService;
        this.vectorStore = vectorStore;
        this.vectorStoreService = vectorStoreService;
        this.chatRepository = chatRepository;
        this.chatHistoryRepository = chatHistoryRepository;
    }

    // Executado ao iniciar a aplicacao
    @EventListener(ApplicationReadyEvent.class)
    public void preencherChatHistory() {
        // busca todos os chats
        List<Chat> chats = chatRepository.findAll();

        // Variavel que armazena todas as mensagens (sera usada para gerar o advisor)
        Map<String, List<Message>> memoria = new HashMap<>();

        // passa por cada chat
        chats.forEach(chat -> {
            // busca as mensagens do chat
            List<Message> messageList = chatHistoryRepository.findByChat_IdOrderByDateAsc(chat.getId()).stream()
                    .map(mensagem -> {
                        ChatHistoryEnum messageType = mensagem.getType();
                        Message message =
                                // verifica se a mensagem e do usuario ou do modelo
                                messageType.equals(ChatHistoryEnum.USER_REQUEST) ?
                                        new UserMessage(mensagem.getMessage()) :
                                        new AssistantMessage(mensagem.getMessage());
                        return message;
                    }).toList();
            // insere as mensagens na variavel do advisor
            memoria.put(chat.getId().toString(), messageList);
        });
        // Cria o advisor e insere o historico de mensagens
        InMemoryChatMemory memoriaSalva = new InMemoryChatMemory();
        memoria.forEach(memoriaSalva::add);
        // Registra o novo advisor
        chatClient = chatClient.mutate().defaultAdvisors(new MessageChatMemoryAdvisor(memoriaSalva)).build();
    }

    @Override
    public ChatHistory chatIA(String query, Integer chatId, List<Integer> documentsIds) {
        try {
            Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("Chat não encontrado com o id " + chatId));
            chatHistoryRepository.save(new ChatHistory(ChatHistoryEnum.USER_REQUEST, query, chat));

            List<String> fileNames = documentosService.getFileNamesFromDocumentsIds(documentsIds);
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
                        .advisors(new QuestionAnswerAdvisor(vectorStore, op != null ? SearchRequest.builder().filterExpression(op.build()).build() : SearchRequest.builder().build()))
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
            List<String> fileNames = documentosService.getFileNamesFromDocumentsIds(documentsIds);

            FilterExpressionBuilder b = new FilterExpressionBuilder();
            FilterExpressionBuilder.Op op = null;
            for (String fileName : fileNames) {
                if (op == null) {
                    op = b.or(b.eq("file_name", fileName), b.eq("source", fileName));
                } else {
                    op = b.or(op, b.or(b.eq("file_name", fileName), b.eq("source", fileName)));
                }
            }
            if(op != null){
                // Fluxo com RAG
                return chatClient
                        .prompt().user(query)
                        .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().filterExpression(op.build()).build()))
                        .stream()
                        .content().map(content -> content.replace(" ", "\u00A0"));
            } else {
                // Fluxo sem RAG
                return chatClient
                        .prompt().user(query)
                        .stream()
                        .content().map(content -> content.replace(" ", "\u00A0"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao se comunidar com a LLM");
        }
    }

    @Transactional
    @Override
    public void processDocumentById(Integer documentId) throws IOException {
        // Busca documento no banco de dados
        Documentos documentos = documentosService.getDocumentById(documentId);
        if (documentos.isProcessed()) throw new RuntimeException("Documento ja processado");
        // Busca documento no sistema
        Path fullPath = documentosService.getFullPath(documentos);
        Resource documentFile = documentosService.getDocument(fullPath);
        if (!documentFile.exists()) throw new RuntimeException("Documento nao encontrado");

        // Inicia a leitura do pdf
        List<Document> documents = processarPDF(documentFile);
        if (CollectionUtils.isEmpty(documents)) throw new RuntimeException("Não foi possível ler o PDF");
        // Após a leitura, divide o texto extraido do pdf em chunks
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<Document> documentosProcessados = null;
        try {
            documentosProcessados = tokenTextSplitter.apply(documents);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Ocorreu um erro ao converter o PDF em chunks");
            throw e;
        }

        if (CollectionUtils.isEmpty(documentosProcessados))
            throw new RuntimeException("Não foi possível processar o PDF");

        // Insere os chunks no banco de dados vetorial
        vectorStore.accept(documentosProcessados);
        // Marca o documento como processado para não ser necessário realizar essa operação novamente
        documentos.setProcessed(true);
        // Vincula o documento com os vetores gerados a partir dele
        documentos.setVetores(findByFileName(documentos.getFileNameWithTimeStamp()));
        documentosService.salvarDocumento(documentos);
    }

    private List<Document> processarPDF(Resource documentFile){
        List<Document> documents;
        try {
            ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader(documentFile);
            documents = pdfReader.get(); // Le o pdf
        } catch (Exception ignored) {
            try {
                PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(documentFile);
                documents = pdfReader.get(); // Le o pdf
            } catch (Exception ignored2) {
                try {
                    TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(documentFile);
                    documents = tikaDocumentReader.get(); // Le o pdf
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Ocorreu um erro ao ler o PDF");
                    throw e;
                }
            }
        }
        return documents;
    }
    @Override
    public Set<VectorStoreEntity> findByFileName(String fileName) {
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
