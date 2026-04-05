import api from './api';
import { AuthResponse, LoginRequest, SignupRequest } from '../types';

export const authService = {
  login: async (req: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', req);
    return response.data;
  },

  signup: async (req: SignupRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/signup', req);
    return response.data;
  },
};
