import backend from './server-config';

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

    static async chatGenerico(chatId:number, query: string) {
        try {
            const response = await backend.post(route + `/chat-generico/${chatId}`, {query:query});
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async chatEmbedding(chatId:number, query: string, documentsIds: number[]) {
        try {
            const response = await backend.post(route + `/chat-embedding/${chatId}`, {query:query, documentsIds: documentsIds});
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
}

export default ChatService;
