package com.lucasgalmeida.llama;

import com.lucasgalmeida.llama.infra.config.HintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints(HintsRegistrar.class)
@SpringBootApplication
public class SpringAiLlamaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAiLlamaApplication.class, args);
	}

}
