import api from './api';
import { UserResponse } from '../types';

export const userService = {
  getAll: (): Promise<UserResponse[]> =>
    api.get<UserResponse[]>('/users').then((r) => r.data),

  getById: (id: number): Promise<UserResponse> =>
    api.get<UserResponse>(`/users/${id}`).then((r) => r.data),
};
