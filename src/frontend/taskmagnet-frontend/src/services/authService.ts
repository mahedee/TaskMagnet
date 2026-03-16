import axios from 'axios';
import { AuthResponse, LoginRequest, SignupRequest } from '../types';

export const authService = {
  login: async (req: LoginRequest): Promise<AuthResponse> => {
    const response = await axios.post<AuthResponse>('/api/auth/login', req);
    return response.data;
  },

  signup: async (req: SignupRequest): Promise<AuthResponse> => {
    const response = await axios.post<AuthResponse>('/api/auth/signup', req);
    return response.data;
  },
};
