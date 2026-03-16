import api from './api';
import { TaskResponse, TaskRequest } from '../types';

export const taskService = {
  getAll: (): Promise<TaskResponse[]> =>
    api.get<TaskResponse[]>('/tasks').then((r) => r.data),

  getById: (id: number): Promise<TaskResponse> =>
    api.get<TaskResponse>(`/tasks/${id}`).then((r) => r.data),

  create: (req: TaskRequest): Promise<TaskResponse> =>
    api.post<TaskResponse>('/tasks', req).then((r) => r.data),

  update: (id: number, req: Partial<TaskRequest>): Promise<TaskResponse> =>
    api.put<TaskResponse>(`/tasks/${id}`, req).then((r) => r.data),

  delete: (id: number): Promise<void> =>
    api.delete(`/tasks/${id}`).then(() => undefined),
};
