import axios from 'axios';
import { useAuthStore } from '@/store/authStore';
const BASE_URL = process.env.EXPO_PUBLIC_API_URL;
console.log('[apiClient] Base URL →', BASE_URL);
if (!BASE_URL) {
  throw new Error('EXPO_PUBLIC_API_URL is not configured. Check your .env file.');
}
export const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
    Version: '8.0',
  },
});

apiClient.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// On 401 — clear auth so app knows token is invalid
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().clearAuth();
    }
    return Promise.reject(error);
  }
);
