import { LoginRequestDTO } from '../types/LoginRequestDTO';
import { RegisterRequestDTO } from '../types/RegisterRequestDTO';
import backend from './server-config';

const route = "/auth";

class AuthService {
    static async login(request:LoginRequestDTO) {
        try {
            const response = await backend.post(route + "/login", request);
            return response.data;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }
    static async register(request:RegisterRequestDTO) {
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
