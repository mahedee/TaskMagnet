import api from './api';
import { SprintResponse, SprintRequest, TaskResponse } from '../types';

export const sprintService = {
  getAll: (): Promise<SprintResponse[]> =>
    api.get<SprintResponse[]>('/sprints').then((r) => r.data),

  getById: (id: number): Promise<SprintResponse> =>
    api.get<SprintResponse>(`/sprints/${id}`).then((r) => r.data),

  create: (req: SprintRequest): Promise<SprintResponse> =>
    api.post<SprintResponse>('/sprints', req).then((r) => r.data),

  update: (id: number, req: Partial<SprintRequest>): Promise<SprintResponse> =>
    api.put<SprintResponse>(`/sprints/${id}`, req).then((r) => r.data),

  delete: (id: number): Promise<void> =>
    api.delete(`/sprints/${id}`).then(() => undefined),

  // Sprint lifecycle operations
  start: (id: number): Promise<SprintResponse> =>
    api.post<SprintResponse>(`/sprints/${id}/start`).then((r) => r.data),

  complete: (id: number): Promise<SprintResponse> =>
    api.post<SprintResponse>(`/sprints/${id}/complete`).then((r) => r.data),

  // Task management within sprint
  getTasks: (id: number): Promise<TaskResponse[]> =>
    api.get<TaskResponse[]>(`/sprints/${id}/tasks`).then((r) => r.data),

  addTask: (sprintId: number, taskId: number): Promise<void> =>
    api.post(`/sprints/${sprintId}/tasks/${taskId}`).then(() => undefined),

  removeTask: (sprintId: number, taskId: number): Promise<void> =>
    api.delete(`/sprints/${sprintId}/tasks/${taskId}`).then(() => undefined),

  // Project-specific sprints
  getByProject: (projectId: number): Promise<SprintResponse[]> =>
    api.get<SprintResponse[]>(`/sprints/project/${projectId}`).then((r) => r.data),
};