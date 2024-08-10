import axios from 'axios';

const backend = axios.create({
    baseURL: import.meta.env.VITE_REACT_APP_API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    }
});

export default backend;


backend.interceptors.request.use((config) => {
    const token = localStorage.getItem('token'); // Retrieve the token from localStorage
    if (token) {
        config.headers.Authorization = `Bearer ${token}`; // Add the token to the Authorization header
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});