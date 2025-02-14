import backend from '../config/server-config';

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

    static async allDocuments() {
        try {
            const response = await backend.get(route);
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

    static async saveDocuments(files:FormData) {
        try {
            const response = await backend.post(route + `/documents`, files, {
                headers: {
                  'Content-Type': 'multipart/form-data',
                },
              })
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async deleteDocumentById(id:number) {
        try {
            const response = await backend.delete(route + `/${id}`);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }

    static async updateDocumentById(id:number, name: string, description: string) {
        try {
            const response = await backend.put(route, {
                id: id,
                name: name,
                description: description,
            });
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }
}

export default DocumentService;
