import backend from './server-config';

const route = "/chat";

class ChatService {
    static async processDocumentById(id:any) {
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

    static async getChatHistoryById(id:any) {
        try {
            const response = await backend.get(route + `/history/${id}`);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async chatEmbedding(chatId:any, query: string) {
        try {
            const response = await backend.post(route + `/chat-embedding/${chatId}`, {query:query});
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async deleteChatById(id:any) {
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
