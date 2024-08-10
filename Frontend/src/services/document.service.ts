import backend from './server-config';

const route = "/document";

class DocumentService {
    static async myDocuments() {
        try {
            const response = await backend.get(route + "/my-documents");
            return response.data;
        } catch (error) {
            console.log(error);
            throw error;
        }
    }

    static async getDocumentById(id:string) {
        try {
            const response = await backend.get(route + `/${id}`);
            return response.data;
        } catch (error) {
            console.log(error);
            throw error;
        }
    }
}

export default DocumentService;
