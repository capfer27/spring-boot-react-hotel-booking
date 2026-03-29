// TODO: Remove this file since it's not being used
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:9090',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Optional: Add a request interceptor for Auth Tokens later
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default api;
