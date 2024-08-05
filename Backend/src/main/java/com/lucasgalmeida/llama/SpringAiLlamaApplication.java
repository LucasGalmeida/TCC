package com.lucasgalmeida.llama;

import com.lucasgalmeida.llama.infra.config.HintsConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints(HintsConfig.class)
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Ollama Spring AI", version = "1", description = "RAG API using Ollama with llama 3.1 and pgVector"))
public class SpringAiLlamaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAiLlamaApplication.class, args);
	}

}
