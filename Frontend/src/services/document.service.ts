import backend from './server-config';

const route = "/document";

class DocumentService {
    static async myDocuments() {
        try {
            const response = await backend.get(route + "/my-documents");
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async getDocumentById(id:string) {
        try {
            const response = await backend.get(route + `/${id}`);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async getResourceById(id:string) {
        try {
            const response = await backend.get(route + `/resource/${id}`, { responseType: 'blob' })
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }
}

export default DocumentService;
