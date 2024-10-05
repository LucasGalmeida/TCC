package com.lucasgalmeida.llama.domain.services.chat;

import com.lucasgalmeida.llama.domain.entities.Chat;
import com.lucasgalmeida.llama.domain.entities.ChatHistory;
import com.lucasgalmeida.llama.domain.entities.VectorStore;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ChatService {
    ChatHistory chatGenerico(String query, Integer chatId);
    ChatHistory chatEmbedding(String query, Integer chatId, List<Integer> documentsIds);
    void processDocumentById(Integer id) throws IOException;
    Set<VectorStore> findByFileName(String fileName);
    Chat createNewChat(String title);
    List<Chat> findAllChatsByUser();
    List<ChatHistory> findChatHistoryByChatId(Integer id);
    void deleteChatById(Integer id);
}
