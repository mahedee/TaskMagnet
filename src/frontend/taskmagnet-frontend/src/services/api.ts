import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const stored = localStorage.getItem('auth');
  if (stored) {
    try {
      const auth = JSON.parse(stored);
      if (auth?.token) {
        config.headers.Authorization = `Bearer ${auth.token}`;
      }
    } catch {
      localStorage.removeItem('auth');
    }
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
