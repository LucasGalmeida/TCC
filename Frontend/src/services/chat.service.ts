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
}

export default ChatService;
