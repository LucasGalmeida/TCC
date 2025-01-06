# Construoi a aplicacao Java usando Maven
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Define o diretorio de trabalho no container
WORKDIR /app

# Copia o arquivo de configuracao do Maven (pom.xml) para o container
COPY pom.xml ./

# Faz o download das dependencias do projeto para acelerar a construcao
RUN mvn dependency:go-offline

# Copia o codigo-fonte da aplicacao para o container
COPY src ./src

# Compila o projeto e empacota a aplicacao, pulando os testes
RUN mvn clean package -DskipTests

# Executa a aplicacao Java com OpenJDK
FROM openjdk:21-jdk-slim

# Define o diretorio de trabalho no container
WORKDIR /app

# Copia o arquivo .jar gerado na etapa 'build' para o diretorio atual
COPY --from=build /app/target/spring-ai-llama-0.0.1-SNAPSHOT.jar spring-ai-llama.jar

# Expoe a porta 8080 (onde a aplicacao Java estara acessivel)
EXPOSE 8080

# Comando para iniciar a aplicacao Java com suporte a depuracao remota
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "spring-ai-llama.jar"]
