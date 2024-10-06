FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/spring-ai-llama-0.0.1-SNAPSHOT.jar spring-ai-llama.jar
EXPOSE 8080
#CMD ["java", "-jar", "spring-ai-llama.jar"]
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "spring-ai-llama.jar"]
