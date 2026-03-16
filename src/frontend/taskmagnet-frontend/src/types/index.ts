// ─── Enums (matching backend exactly) ──────────────────────────────────────

export type ProjectStatus = 'PLANNING' | 'ACTIVE' | 'ON_HOLD' | 'COMPLETED' | 'CANCELLED' | 'ARCHIVED';
export type TaskStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'IN_REVIEW' | 'ON_HOLD' | 'COMPLETED' | 'APPROVED' | 'REJECTED' | 'CANCELLED';
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT' | 'CRITICAL';

// ─── Auth ────────────────────────────────────────────────────────────────────

export interface AuthResponse {
  id: number;
  token: string;
  tokenType: string;
  expiresIn: number;
  username: string;
  email: string;
  fullName: string;
  roles: string[];
}

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface SignupRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  department?: string;
  jobTitle?: string;
}

// ─── User ────────────────────────────────────────────────────────────────────

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  phoneNumber?: string;
  department?: string;
  jobTitle?: string;
  isEmailVerified: boolean;
  isActive: boolean;
  lastLoginDate?: string;
  roles: string[];
  createdAt: string;
  updatedAt: string;
}

// ─── Category ────────────────────────────────────────────────────────────────

export interface CategoryResponse {
  id: number;
  name: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CategoryRequest {
  name: string;
  description?: string;
}

// ─── Project ─────────────────────────────────────────────────────────────────

export interface ProjectResponse {
  id: number;
  name: string;
  code: string;
  description?: string;
  status: ProjectStatus;
  startDate?: string;
  endDate?: string;
  targetCompletionDate?: string;
  actualCompletionDate?: string;
  budget?: number;
  colorCode?: string;
  progressPercentage?: number;
  ownerId: number;
  ownerUsername: string;
  categoryId?: number;
  categoryName?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectRequest {
  name: string;
  code: string;
  description?: string;
  status?: ProjectStatus;
  startDate?: string;
  endDate?: string;
  targetCompletionDate?: string;
  budget?: number;
  colorCode?: string;
  ownerId: number;
  categoryId?: number;
}

// ─── Task ─────────────────────────────────────────────────────────────────────

export interface TaskResponse {
  id: number;
  title: string;
  description?: string;
  status: TaskStatus;
  priority: Priority;
  dueDate?: string;
  startDate?: string;
  completionDate?: string;
  estimatedHours?: number;
  actualHours?: number;
  progressPercentage?: number;
  notes?: string;
  tags?: string;
  isBillable?: boolean;
  assignedToId?: number;
  assignedToUsername?: string;
  createdById: number;
  createdByUsername: string;
  projectId?: number;
  projectName?: string;
  categoryId?: number;
  categoryName?: string;
  parentTaskId?: number;
  parentTaskTitle?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface TaskRequest {
  title: string;
  description?: string;
  status?: TaskStatus;
  priority?: Priority;
  dueDate?: string;
  startDate?: string;
  estimatedHours?: number;
  notes?: string;
  tags?: string;
  isBillable?: boolean;
  assignedToId?: number;
  createdById: number;
  projectId?: number;
  categoryId?: number;
  parentTaskId?: number;
}

