import backend from './server-config';

const route = "/auth";

class AuthService {
    static async login(request:any) {
        try {
            const response = await backend.post(route + "/login", request);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }
    static async register(request:any) {
        try {
            const response = await backend.post(route + "/register", request);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }
}

export default AuthService;
