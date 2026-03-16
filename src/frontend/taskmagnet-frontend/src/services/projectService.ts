import api from './api';
import { ProjectResponse, ProjectRequest } from '../types';

export const projectService = {
  getAll: (): Promise<ProjectResponse[]> =>
    api.get<ProjectResponse[]>('/projects').then((r) => r.data),

  getById: (id: number): Promise<ProjectResponse> =>
    api.get<ProjectResponse>(`/projects/${id}`).then((r) => r.data),

  create: (req: ProjectRequest): Promise<ProjectResponse> =>
    api.post<ProjectResponse>('/projects', req).then((r) => r.data),

  update: (id: number, req: Partial<ProjectRequest>): Promise<ProjectResponse> =>
    api.put<ProjectResponse>(`/projects/${id}`, req).then((r) => r.data),

  delete: (id: number): Promise<void> =>
    api.delete(`/projects/${id}`).then(() => undefined),
};
