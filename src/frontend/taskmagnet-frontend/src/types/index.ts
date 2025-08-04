// Core Types for TaskMagnet Frontend
export interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'USER' | 'ADMIN';
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Category {
  id: string;
  name: string;
  description?: string;
  color: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Project {
  id: string;
  name: string;
  description?: string;
  startDate?: string;
  endDate?: string;
  status: 'PLANNING' | 'IN_PROGRESS' | 'COMPLETED' | 'ON_HOLD' | 'CANCELLED';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  ownerId: string;
  categoryId?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Task {
  id: string;
  title: string;
  description?: string;
  dueDate?: string;
  status: 'TODO' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  projectId: string;
  assigneeId?: string;
  categoryId?: string;
  estimatedHours?: number;
  actualHours?: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface TaskComment {
  id: string;
  taskId: string;
  userId: string;
  comment: string;
  createdAt: string;
}

export interface TaskAttachment {
  id: string;
  taskId: string;
  fileName: string;
  filePath: string;
  fileSize: number;
  uploadedBy: string;
  uploadedAt: string;
}

// Request/Response types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

// Form types for creating/updating entities
export interface CreateProjectRequest {
  name: string;
  description?: string;
  startDate?: string;
  endDate?: string;
  status: Project['status'];
  priority: Project['priority'];
  categoryId?: string;
}

export interface UpdateProjectRequest extends Partial<CreateProjectRequest> {
  id: string;
}

export interface CreateTaskRequest {
  title: string;
  description?: string;
  dueDate?: string;
  status: Task['status'];
  priority: Task['priority'];
  projectId: string;
  assigneeId?: string;
  categoryId?: string;
  estimatedHours?: number;
}

export interface UpdateTaskRequest extends Partial<CreateTaskRequest> {
  id: string;
}

export interface CreateCategoryRequest {
  name: string;
  description?: string;
  color: string;
}

export interface UpdateCategoryRequest extends Partial<CreateCategoryRequest> {
  id: string;
}
