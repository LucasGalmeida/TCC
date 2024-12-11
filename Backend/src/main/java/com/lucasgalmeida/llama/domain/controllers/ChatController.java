package com.lucasgalmeida.llama.domain.controllers;


import com.lucasgalmeida.llama.application.dto.RequestDTO;
import com.lucasgalmeida.llama.domain.entities.Chat;
import com.lucasgalmeida.llama.domain.entities.ChatHistory;
import com.lucasgalmeida.llama.domain.services.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat-ia/{chatId}")
    public ResponseEntity<ChatHistory> chatIA(@RequestBody RequestDTO request, @PathVariable Integer chatId) {
        return ResponseEntity.ok(chatService.chatIA(request.query(), chatId, request.documentsIds()));
    }

    @PostMapping("/process/{id}")
    public ResponseEntity<?> processDocumentById(@PathVariable Integer id) throws IOException {
        chatService.processDocumentById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/file-name")
    public ResponseEntity<?> findByFileName(@RequestParam String fileName) {
        return ResponseEntity.ok(chatService.findByFileName(fileName));
    }

    @PostMapping("/{title}")
    public ResponseEntity<Chat> createNewChat(@PathVariable String title) {
        return ResponseEntity.ok(chatService.createNewChat(title));
    }

    @PutMapping("/change-title/{id}")
    public ResponseEntity<Chat> changeTitle(@PathVariable Integer id, @RequestParam String title) {
        return ResponseEntity.ok(chatService.changeTitle(id, title));
    }

    @GetMapping
    public ResponseEntity<List<Chat>> myChats() {
        return ResponseEntity.ok(chatService.findAllChatsByUser());
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<List<ChatHistory>> chatHistory(@PathVariable Integer id) {
        return ResponseEntity.ok(chatService.findChatHistoryByChatId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChatById(@PathVariable Integer id) {
        chatService.deleteChatById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/last-chat-history/{id}")
    public ResponseEntity<?> deleteLastChatHistoryByChatId(@PathVariable Integer id) {
        chatService.deleteLastChatHistoryByChatId(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/meu-professor-responde", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> meuProfessorRespnde(@RequestParam String query, @RequestParam List<Integer> documentsIds) {
        return chatService.chatWithStream(query, documentsIds);
    }
}
