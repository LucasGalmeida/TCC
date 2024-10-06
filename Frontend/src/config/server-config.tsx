import axios from 'axios';
import { notification } from "antd";

const backend = axios.create({
    baseURL: import.meta.env.VITE_REACT_APP_API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    }
});

export default backend;

// Interceptor para enviar o token
backend.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});


// Interceptor para capturar erros
backend.interceptors.response.use(
    (response) => {
      return response;
    },
    (error) => {
      
      if (error.response) {
        const { status } = error.response;
  
        // Tratamento para erro 401 (não autorizado)
        if (status === 401) {
          notification.error({
            message: "Não Autorizado",
            description: "Sua sessão expirou. Faça login novamente.",
          });
          window.localStorage.removeItem("token");
          window.location.href = "/login";
        }
  
        // Tratamento para erro 500 (erro interno do servidor)
        if (status === 500) {
          notification.error({
            message: "Erro Interno do Servidor",
            description: "Ocorreu um erro no servidor. Por favor, tente novamente mais tarde.",
          });
        }
      } else {
        // Caso não tenha uma resposta (ex: problemas de rede)
        notification.error({
          message: "Erro de Conexão",
          description: "Não foi possível conectar ao servidor. Verifique sua conexão de internet.",
        });
      }
  
      // Retorne a Promise com erro para ser tratado (se necessário) nas chamadas
      return Promise.reject(error);
    }
  );