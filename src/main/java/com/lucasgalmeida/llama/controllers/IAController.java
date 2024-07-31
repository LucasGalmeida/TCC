package com.lucasgalmeida.llama.controllers;


import com.lucasgalmeida.llama.dto.RequestDTO;
import com.lucasgalmeida.llama.dto.ResponseDTO;
import com.lucasgalmeida.llama.services.IAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ia")
public class IAController {

    private final IAService iAService;

    @PostMapping("/chat-generico")
    public ResponseEntity<ResponseDTO> chat(@RequestBody RequestDTO request) {
        String chatResponse = iAService.chatGenerico(request.query());
        ResponseDTO response = new ResponseDTO("Success", chatResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarLeituraDeDocumentos() {
        iAService.iniciaLeituraDeDocumentos();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/chat-especifico")
    public ResponseEntity<ResponseDTO> chatEspecifico(@RequestBody RequestDTO request) {
        String chatResponse = iAService.chatEspecifico(request.query());
        ResponseDTO response = new ResponseDTO("Success", chatResponse);
        return ResponseEntity.ok(response);
    }

}
