package com.lucasgalmeida.llama.controllers;


import com.lucasgalmeida.llama.dto.RequestDTO;
import com.lucasgalmeida.llama.dto.ResponseDTO;
import com.lucasgalmeida.llama.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ResponseDTO> chat(@RequestBody RequestDTO request) {
        String chatResponse = chatService.chat(request.query());
        ResponseDTO response = new ResponseDTO("Success", chatResponse);
        return ResponseEntity.ok(response);

    }
}
