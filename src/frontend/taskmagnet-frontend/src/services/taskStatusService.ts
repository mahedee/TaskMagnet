import api from './api';
import { TaskStatusResponse, TaskStatusRequest } from '../types';

export const taskStatusService = {
  getAll: (): Promise<TaskStatusResponse[]> =>
    api.get<TaskStatusResponse[]>('/task-statuses').then((r) => r.data),

  getByProjectId: (projectId: number): Promise<TaskStatusResponse[]> =>
    api.get<TaskStatusResponse[]>(`/task-statuses/project/${projectId}`).then((r) => r.data),

  getByProjectIdAndClosedStatus: (projectId: number, isClosedStatus: boolean): Promise<TaskStatusResponse[]> =>
    api.get<TaskStatusResponse[]>(`/task-statuses/project/${projectId}/closed/${isClosedStatus}`).then((r) => r.data),

  getById: (id: number): Promise<TaskStatusResponse> =>
    api.get<TaskStatusResponse>(`/task-statuses/${id}`).then((r) => r.data),

  create: (req: TaskStatusRequest): Promise<TaskStatusResponse> =>
    api.post<TaskStatusResponse>('/task-statuses', req).then((r) => r.data),

  update: (id: number, req: Partial<TaskStatusRequest>): Promise<TaskStatusResponse> =>
    api.put<TaskStatusResponse>(`/task-statuses/${id}`, req).then((r) => r.data),

  delete: (id: number): Promise<void> =>
    api.delete(`/task-statuses/${id}`).then(() => undefined),

  reorderStatuses: (projectId: number, statusIds: number[]): Promise<void> =>
    api.put(`/task-statuses/project/${projectId}/reorder`, { statusIds }).then(() => undefined),
};