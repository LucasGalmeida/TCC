# ollama-spring-ai
Aplicação que implementa o RAG utilizando Spring AI com o modelo Llama 3.2. Frontend utiliza React. Banco de dados postgres.

Requisitos:
- Docker desktop

Para rodar a aplicação, execute:
docker-compose up --build

---------------------------------------------------------------------------------------------------
Caso você queira rodar localmente (sem docker), você vai precisar:
- Postgres com a extenção do vector database 16.
- Java 21
- React
- VSCode (para rodar o frontend) - utilize npm install seguido de npm run dev
- Intellij (para rodar o backend) - abra o código no intellij e execute a aplicação
