package com.lucasgalmeida.llama.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final OllamaChatModel chatModel;

    @Value("classpath:/static/prompt_one.st")
    private Resource promptOne;

    public String chat(String query) {
        PromptTemplate promptTemplate = new PromptTemplate(promptOne);
        Prompt prompt = promptTemplate.create(Map.of("input", query));
        String response = chatModel.call(prompt).getResult().getOutput().getContent();
        return response;
    }

}
