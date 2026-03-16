import api from './api';
import { CategoryResponse, CategoryRequest } from '../types';

export const categoryService = {
  getAll: (): Promise<CategoryResponse[]> =>
    api.get<CategoryResponse[]>('/categories').then((r) => r.data),

  getById: (id: number): Promise<CategoryResponse> =>
    api.get<CategoryResponse>(`/categories/${id}`).then((r) => r.data),

  create: (req: CategoryRequest): Promise<CategoryResponse> =>
    api.post<CategoryResponse>('/categories', req).then((r) => r.data),

  update: (id: number, req: Partial<CategoryRequest>): Promise<CategoryResponse> =>
    api.put<CategoryResponse>(`/categories/${id}`, req).then((r) => r.data),

  delete: (id: number): Promise<void> =>
    api.delete(`/categories/${id}`).then(() => undefined),
};
