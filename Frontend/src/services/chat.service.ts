import backend from '../config/server-config';

const route = "/chat";

class ChatService {
    static async processDocumentById(id:number) {
        try {
            const response = await backend.post(route + `/process/${id}`, null);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async myChats() {
        try {
            const response = await backend.get(route);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async newChat(title:string) {
        try {
            const response = await backend.post(route + `/${title}`);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async getChatHistoryById(id:number) {
        try {
            const response = await backend.get(route + `/history/${id}`);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async chatIa(chatId:number, query: string, documentsIds: number[]) {
        try {
            const response = await backend.post(route + `/chat-ia/${chatId}`, {query:query, documentsIds: documentsIds});
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async deleteChatById(id:number) {
        try {
            const response = await backend.delete(route + `/${id}`);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async deleteLastChatHistoryByChatId(id:number) {
        try {
            const response = await backend.delete(route + `/last-chat-history/${id}`);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async updateTitle(id: number, title: string) {
        try {
          const response = await backend.put(`${route}/change-title/${id}?title=${encodeURIComponent(title)}`);
          return response.data;
        } catch (error) {
          console.error(error);
          throw error;
        }
    }
     
    static chamadaStream(query:string, documentsIds: number){
        const baseURL = backend.defaults.baseURL;
        const aux = `${baseURL}${route}/meu-professor-responde?query=${encodeURIComponent(query)}&documentsIds=${documentsIds}`;
        return new EventSource(aux);
    }

}

export default ChatService;
