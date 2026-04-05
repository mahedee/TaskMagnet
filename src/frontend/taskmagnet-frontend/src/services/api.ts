import axios from 'axios';

// Create a function to handle auth cleanup
const handleAuthExpiration = () => {
  localStorage.removeItem('auth');
  localStorage.removeItem('loginTime');
  // Instead of direct redirect, trigger a page reload to let React Router handle it
  if (!window.location.pathname.includes('/login')) {
    window.location.reload();
  }
};

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  // Don't add Authorization header to login/signup endpoints
  const isAuthEndpoint = config.url?.includes('/auth/login') || config.url?.includes('/auth/signup');
  
  if (!isAuthEndpoint) {
    const stored = localStorage.getItem('auth');
    if (stored) {
      try {
        const auth = JSON.parse(stored);
        if (auth?.token) {
          config.headers.Authorization = `Bearer ${auth.token}`;
          console.log('Added Bearer token to request:', config.url);
        }
      } catch (error) {
        console.log('Error parsing auth for request, clearing:', error);
        localStorage.removeItem('auth');
        localStorage.removeItem('loginTime');
      }
    }
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      console.log('Received 401 unauthorized, handling auth expiration');
      handleAuthExpiration();
    }
    return Promise.reject(error);
  }
);

export default api;
