# Constroi a aplicacao usando Node.js
FROM node:20 AS build 

# Define o diretorio de trabalho no container
WORKDIR /app

# Copia os arquivos de dependencias para o container
COPY package*.json ./  
COPY package-lock.json ./  

# Instala as dependencias da aplicacao
RUN npm install  

# Copia o restante do codigo da aplicacao para o container
COPY . .  

# Executa o comando de build da aplicacao (compila o frontend)
RUN npm run build  

# Serve a aplicacao com Nginx
FROM nginx:alpine

# Copia os arquivos compilados na etapa 'build' para o diretorio padrao do Nginx
COPY --from=build /app/dist /usr/share/nginx/html  

# Expoe a porta 80 (onde o Nginx serve a aplicacao)
EXPOSE 80  

# Comando para iniciar o Nginx em primeiro plano
CMD ["nginx", "-g", "daemon off;"]  
