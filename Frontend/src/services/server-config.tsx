import axios from 'axios';

const backend = axios.create({
    baseURL: import.meta.env.REACT_APP_API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    }
});

export default backend;
