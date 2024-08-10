package com.lucasgalmeida.llama.domain.controllers;


import com.lucasgalmeida.llama.application.dto.RequestDTO;
import com.lucasgalmeida.llama.application.dto.ResponseDTO;
import com.lucasgalmeida.llama.domain.entities.Chat;
import com.lucasgalmeida.llama.domain.entities.ChatHistory;
import com.lucasgalmeida.llama.domain.entities.Document;
import com.lucasgalmeida.llama.domain.services.chat.ChatService;
import com.lucasgalmeida.llama.domain.services.chat.impl.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat-generico")
    public ResponseEntity<ResponseDTO> chat(@RequestBody RequestDTO request) {
        String chatResponse = chatService.chatGenerico(request.query());
        ResponseDTO response = new ResponseDTO("Success", chatResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process/{id}")
    public ResponseEntity<?> processDocumentById(@PathVariable Integer id) throws IOException {
        chatService.processDocumentById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/chat-especifico")
    public ResponseEntity<ResponseDTO> chatEspecifico(@RequestBody RequestDTO request) {
        String chatResponse = chatService.chatEspecifico(request.query());
        ResponseDTO response = new ResponseDTO("Success", chatResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat-embedding/{chatId}")
    public ResponseEntity<ChatHistory> chatEmbedding(@RequestBody RequestDTO request, @PathVariable Integer chatId) {
        return ResponseEntity.ok(chatService.chatEmbedding(request.query(), chatId));
    }

    @GetMapping("/file-name")
    public ResponseEntity<?> findByFileName(@RequestParam String fileName) {
        return ResponseEntity.ok(chatService.findByFileName(fileName));
    }

    @PostMapping("/{title}")
    public ResponseEntity<Chat> createNewChat(@PathVariable String title){
        return ResponseEntity.ok(chatService.createNewChat(title));
    }

    @GetMapping
    public ResponseEntity<List<Chat>> myChats() {
        return ResponseEntity.ok(chatService.findAllChatsByUser());
    }
}
