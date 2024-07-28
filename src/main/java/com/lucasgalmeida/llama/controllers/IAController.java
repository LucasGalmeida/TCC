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

    @PostMapping("/chat")
    public ResponseEntity<ResponseDTO> chat(@RequestBody RequestDTO request) {
        String chatResponse = iAService.chat(request.query());
        ResponseDTO response = new ResponseDTO("Success", chatResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarLeituraDeDocumentos() {
        iAService.iniciaLeituraDeDocumentos();
        return ResponseEntity.ok().build();
    }
}
