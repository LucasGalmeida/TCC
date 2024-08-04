package com.lucasgalmeida.llama.domain.controllers;


import com.lucasgalmeida.llama.application.dto.RequestDTO;
import com.lucasgalmeida.llama.application.dto.ResponseDTO;
import com.lucasgalmeida.llama.domain.services.ia.impl.IAServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ia")
public class IAController {

    private final IAServiceImpl iAService;

    @PostMapping("/chat-generico")
    public ResponseEntity<ResponseDTO> chat(@RequestBody RequestDTO request) {
        String chatResponse = iAService.chatGenerico(request.query());
        ResponseDTO response = new ResponseDTO("Success", chatResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process/{id}")
    public ResponseEntity<?> processDocumentById(@PathVariable Integer id) throws IOException {
        iAService.processDocumentById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/chat-especifico")
    public ResponseEntity<ResponseDTO> chatEspecifico(@RequestBody RequestDTO request) {
        String chatResponse = iAService.chatEspecifico(request.query());
        ResponseDTO response = new ResponseDTO("Success", chatResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/file-name")
    public ResponseEntity<?> findByFileName(@RequestParam String fileName) {
        return ResponseEntity.ok(iAService.findByFileName(fileName));
    }

}
