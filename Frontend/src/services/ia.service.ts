import backend from './server-config';

const route = "/ia";

class IAService {
    static async processDocumentById(id:any) {
        try {
            const response = await backend.post(route + `/process/${id}`, null);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }
}

export default IAService;
