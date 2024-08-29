import axios from 'axios';

const backend = axios.create({
    baseURL: import.meta.env.VITE_REACT_APP_API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    }
});

export default backend;


backend.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});