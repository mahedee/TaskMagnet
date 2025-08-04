import { 
  User, Category, Project, Task, TaskComment, TaskAttachment,
  CreateProjectRequest, UpdateProjectRequest,
  CreateTaskRequest, UpdateTaskRequest,
  CreateCategoryRequest, UpdateCategoryRequest 
} from '../types';

// Mock data for development - this will be replaced with API calls later
let mockUsers: User[] = [
  {
    id: '1',
    username: 'admin',
    email: 'admin@taskmagnet.com',
    firstName: 'Admin',
    lastName: 'User',
    role: 'ADMIN',
    isActive: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  },
  {
    id: '2',
    username: 'johndoe',
    email: 'john.doe@taskmagnet.com',
    firstName: 'John',
    lastName: 'Doe',
    role: 'USER',
    isActive: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  }
];

let mockCategories: Category[] = [
  {
    id: '1',
    name: 'Development',
    description: 'Software development tasks',
    color: '#3498db',
    isActive: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  },
  {
    id: '2',
    name: 'Testing',
    description: 'Quality assurance and testing',
    color: '#e74c3c',
    isActive: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  },
  {
    id: '3',
    name: 'Documentation',
    description: 'Project documentation',
    color: '#f39c12',
    isActive: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  }
];

let mockProjects: Project[] = [
  {
    id: '1',
    name: 'TaskMagnet MVP',
    description: 'Minimum viable product for TaskMagnet project management system',
    startDate: '2024-01-01',
    endDate: '2024-03-31',
    status: 'IN_PROGRESS',
    priority: 'HIGH',
    ownerId: '1',
    categoryId: '1',
    isActive: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  },
  {
    id: '2',
    name: 'Documentation Portal',
    description: 'Create comprehensive documentation for the system',
    startDate: '2024-02-01',
    endDate: '2024-04-15',
    status: 'PLANNING',
    priority: 'MEDIUM',
    ownerId: '2',
    categoryId: '3',
    isActive: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  }
];

let mockTasks: Task[] = [
  {
    id: '1',
    title: 'Set up React Frontend',
    description: 'Create React TypeScript application with basic routing',
    dueDate: '2024-01-15',
    status: 'IN_PROGRESS',
    priority: 'HIGH',
    projectId: '1',
    assigneeId: '2',
    categoryId: '1',
    estimatedHours: 16,
    actualHours: 8,
    isActive: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  },
  {
    id: '2',
    title: 'Design Database Schema',
    description: 'Create comprehensive database design for all entities',
    dueDate: '2024-01-10',
    status: 'COMPLETED',
    priority: 'HIGH',
    projectId: '1',
    assigneeId: '1',
    categoryId: '1',
    estimatedHours: 8,
    actualHours: 6,
    isActive: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  },
  {
    id: '3',
    title: 'API Documentation',
    description: 'Document all REST endpoints with examples',
    dueDate: '2024-02-15',
    status: 'TODO',
    priority: 'MEDIUM',
    projectId: '2',
    assigneeId: '2',
    categoryId: '3',
    estimatedHours: 12,
    isActive: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  }
];

let mockComments: TaskComment[] = [];
let mockAttachments: TaskAttachment[] = [];

// Utility functions
const generateId = (): string => {
  return Math.random().toString(36).substr(2, 9);
};

const getCurrentTimestamp = (): string => {
  return new Date().toISOString();
};

// Simulated async behavior
const delay = (ms: number = 300): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, ms));
};

// Repository class for JSON-based data management
export class JsonRepository {
  
  // User methods
  async getUsers(): Promise<User[]> {
    await delay();
    return [...mockUsers];
  }

  async getUserById(id: string): Promise<User | null> {
    await delay();
    return mockUsers.find(user => user.id === id) || null;
  }

  // Category methods
  async getCategories(): Promise<Category[]> {
    await delay();
    return mockCategories.filter(cat => cat.isActive);
  }

  async getCategoryById(id: string): Promise<Category | null> {
    await delay();
    return mockCategories.find(cat => cat.id === id && cat.isActive) || null;
  }

  async createCategory(request: CreateCategoryRequest): Promise<Category> {
    await delay();
    const category: Category = {
      id: generateId(),
      ...request,
      isActive: true,
      createdAt: getCurrentTimestamp(),
      updatedAt: getCurrentTimestamp()
    };
    mockCategories.push(category);
    return category;
  }

  async updateCategory(request: UpdateCategoryRequest): Promise<Category | null> {
    await delay();
    const index = mockCategories.findIndex(cat => cat.id === request.id);
    if (index === -1) return null;

    const updatedCategory = {
      ...mockCategories[index],
      ...request,
      updatedAt: getCurrentTimestamp()
    };
    mockCategories[index] = updatedCategory;
    return updatedCategory;
  }

  async deleteCategory(id: string): Promise<boolean> {
    await delay();
    const index = mockCategories.findIndex(cat => cat.id === id);
    if (index === -1) return false;

    mockCategories[index] = {
      ...mockCategories[index],
      isActive: false,
      updatedAt: getCurrentTimestamp()
    };
    return true;
  }

  // Project methods
  async getProjects(): Promise<Project[]> {
    await delay();
    return mockProjects.filter(proj => proj.isActive);
  }

  async getProjectById(id: string): Promise<Project | null> {
    await delay();
    return mockProjects.find(proj => proj.id === id && proj.isActive) || null;
  }

  async getProjectsByOwner(ownerId: string): Promise<Project[]> {
    await delay();
    return mockProjects.filter(proj => proj.ownerId === ownerId && proj.isActive);
  }

  async createProject(request: CreateProjectRequest, ownerId: string): Promise<Project> {
    await delay();
    const project: Project = {
      id: generateId(),
      ...request,
      ownerId,
      isActive: true,
      createdAt: getCurrentTimestamp(),
      updatedAt: getCurrentTimestamp()
    };
    mockProjects.push(project);
    return project;
  }

  async updateProject(request: UpdateProjectRequest): Promise<Project | null> {
    await delay();
    const index = mockProjects.findIndex(proj => proj.id === request.id);
    if (index === -1) return null;

    const updatedProject = {
      ...mockProjects[index],
      ...request,
      updatedAt: getCurrentTimestamp()
    };
    mockProjects[index] = updatedProject;
    return updatedProject;
  }

  async deleteProject(id: string): Promise<boolean> {
    await delay();
    const index = mockProjects.findIndex(proj => proj.id === id);
    if (index === -1) return false;

    mockProjects[index] = {
      ...mockProjects[index],
      isActive: false,
      updatedAt: getCurrentTimestamp()
    };
    return true;
  }

  // Task methods
  async getTasks(): Promise<Task[]> {
    await delay();
    return mockTasks.filter(task => task.isActive);
  }

  async getTaskById(id: string): Promise<Task | null> {
    await delay();
    return mockTasks.find(task => task.id === id && task.isActive) || null;
  }

  async getTasksByProject(projectId: string): Promise<Task[]> {
    await delay();
    return mockTasks.filter(task => task.projectId === projectId && task.isActive);
  }

  async getTasksByAssignee(assigneeId: string): Promise<Task[]> {
    await delay();
    return mockTasks.filter(task => task.assigneeId === assigneeId && task.isActive);
  }

  async createTask(request: CreateTaskRequest): Promise<Task> {
    await delay();
    const task: Task = {
      id: generateId(),
      ...request,
      isActive: true,
      createdAt: getCurrentTimestamp(),
      updatedAt: getCurrentTimestamp()
    };
    mockTasks.push(task);
    return task;
  }

  async updateTask(request: UpdateTaskRequest): Promise<Task | null> {
    await delay();
    const index = mockTasks.findIndex(task => task.id === request.id);
    if (index === -1) return null;

    const updatedTask = {
      ...mockTasks[index],
      ...request,
      updatedAt: getCurrentTimestamp()
    };
    mockTasks[index] = updatedTask;
    return updatedTask;
  }

  async deleteTask(id: string): Promise<boolean> {
    await delay();
    const index = mockTasks.findIndex(task => task.id === id);
    if (index === -1) return false;

    mockTasks[index] = {
      ...mockTasks[index],
      isActive: false,
      updatedAt: getCurrentTimestamp()
    };
    return true;
  }

  // Statistics and dashboard methods
  async getDashboardStats(userId?: string): Promise<{
    totalProjects: number;
    totalTasks: number;
    completedTasks: number;
    inProgressTasks: number;
    overdueTasks: number;
  }> {
    await delay();
    
    const projects = userId 
      ? mockProjects.filter(p => p.ownerId === userId && p.isActive)
      : mockProjects.filter(p => p.isActive);
    
    const tasks = userId
      ? mockTasks.filter(t => t.assigneeId === userId && t.isActive)
      : mockTasks.filter(t => t.isActive);

    const now = new Date();
    const overdueTasks = tasks.filter(t => 
      t.dueDate && new Date(t.dueDate) < now && t.status !== 'COMPLETED'
    );

    return {
      totalProjects: projects.length,
      totalTasks: tasks.length,
      completedTasks: tasks.filter(t => t.status === 'COMPLETED').length,
      inProgressTasks: tasks.filter(t => t.status === 'IN_PROGRESS').length,
      overdueTasks: overdueTasks.length
    };
  }
}

// Export singleton instance
export const jsonRepository = new JsonRepository();
