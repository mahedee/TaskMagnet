import React, { useState, useEffect } from 'react';
import { jsonRepository } from '../services/jsonRepository';
import { Task, Project, User, Category, CreateTaskRequest, UpdateTaskRequest } from '../types';
import './TaskManager.css';

const TaskManager: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [selectedProject, setSelectedProject] = useState<string>('');
  const [selectedStatus, setSelectedStatus] = useState<string>('');
  const [selectedPriority, setSelectedPriority] = useState<string>('');
  const [searchTerm, setSearchTerm] = useState('');

  // Form state
  const [formData, setFormData] = useState<CreateTaskRequest>({
    title: '',
    description: '',
    dueDate: '',
    status: 'TODO',
    priority: 'MEDIUM',
    projectId: '',
    assigneeId: '',
    categoryId: '',
    estimatedHours: undefined
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [tasksData, projectsData, usersData, categoriesData] = await Promise.all([
        jsonRepository.getTasks(),
        jsonRepository.getProjects(),
        jsonRepository.getUsers(),
        jsonRepository.getCategories()
      ]);
      setTasks(tasksData);
      setProjects(projectsData);
      setUsers(usersData);
      setCategories(categoriesData);
    } catch (error) {
      console.error('Error loading data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingTask) {
        const updateRequest: UpdateTaskRequest = {
          id: editingTask.id,
          ...formData
        };
        await jsonRepository.updateTask(updateRequest);
      } else {
        await jsonRepository.createTask(formData);
      }
      
      await loadData();
      resetForm();
    } catch (error) {
      console.error('Error saving task:', error);
    }
  };

  const handleEdit = (task: Task) => {
    setEditingTask(task);
    setFormData({
      title: task.title,
      description: task.description || '',
      dueDate: task.dueDate || '',
      status: task.status,
      priority: task.priority,
      projectId: task.projectId,
      assigneeId: task.assigneeId || '',
      categoryId: task.categoryId || '',
      estimatedHours: task.estimatedHours
    });
    setShowForm(true);
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        await jsonRepository.deleteTask(id);
        await loadData();
      } catch (error) {
        console.error('Error deleting task:', error);
      }
    }
  };

  const handleStatusChange = async (taskId: string, newStatus: Task['status']) => {
    try {
      const task = tasks.find(t => t.id === taskId);
      if (task) {
        const updateRequest: UpdateTaskRequest = {
          id: taskId,
          status: newStatus
        };
        await jsonRepository.updateTask(updateRequest);
        await loadData();
      }
    } catch (error) {
      console.error('Error updating task status:', error);
    }
  };

  const resetForm = () => {
    setFormData({
      title: '',
      description: '',
      dueDate: '',
      status: 'TODO',
      priority: 'MEDIUM',
      projectId: '',
      assigneeId: '',
      categoryId: '',
      estimatedHours: undefined
    });
    setEditingTask(null);
    setShowForm(false);
  };

  const getStatusColor = (status: string): string => {
    switch (status) {
      case 'COMPLETED': return '#27ae60';
      case 'IN_PROGRESS': return '#3498db';
      case 'TODO': return '#95a5a6';
      case 'CANCELLED': return '#e74c3c';
      default: return '#95a5a6';
    }
  };

  const getPriorityColor = (priority: string): string => {
    switch (priority) {
      case 'URGENT': return '#e74c3c';
      case 'HIGH': return '#f39c12';
      case 'MEDIUM': return '#3498db';
      case 'LOW': return '#27ae60';
      default: return '#95a5a6';
    }
  };

  const getProjectName = (projectId: string): string => {
    const project = projects.find(p => p.id === projectId);
    return project?.name || 'Unknown Project';
  };

  const getAssigneeName = (assigneeId?: string): string => {
    if (!assigneeId) return 'Unassigned';
    const user = users.find(u => u.id === assigneeId);
    return user ? `${user.firstName} ${user.lastName}` : 'Unknown User';
  };

  const getCategoryName = (categoryId?: string): string => {
    if (!categoryId) return 'No Category';
    const category = categories.find(c => c.id === categoryId);
    return category?.name || 'Unknown Category';
  };

  const isOverdue = (dueDate?: string): boolean => {
    if (!dueDate) return false;
    return new Date(dueDate) < new Date() && formData.status !== 'COMPLETED';
  };

  // Filter tasks based on search and filters
  const filteredTasks = tasks.filter(task => {
    const matchesSearch = task.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         task.description?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesProject = !selectedProject || task.projectId === selectedProject;
    const matchesStatus = !selectedStatus || task.status === selectedStatus;
    const matchesPriority = !selectedPriority || task.priority === selectedPriority;
    
    return matchesSearch && matchesProject && matchesStatus && matchesPriority;
  });

  // Group tasks by status for kanban-style view
  const tasksByStatus = {
    TODO: filteredTasks.filter(t => t.status === 'TODO'),
    IN_PROGRESS: filteredTasks.filter(t => t.status === 'IN_PROGRESS'),
    COMPLETED: filteredTasks.filter(t => t.status === 'COMPLETED'),
    CANCELLED: filteredTasks.filter(t => t.status === 'CANCELLED')
  };

  if (loading) {
    return (
      <div className="task-manager-loading">
        <div className="spinner"></div>
        <p>Loading tasks...</p>
      </div>
    );
  }

  return (
    <div className="task-manager">
      <header className="task-manager-header">
        <h1>Task Management</h1>
        <button 
          className="btn btn-primary"
          onClick={() => setShowForm(true)}
        >
          + New Task
        </button>
      </header>

      {/* Filters and Search */}
      <div className="task-filters">
        <div className="search-box">
          <input
            type="text"
            placeholder="Search tasks..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="form-control"
          />
        </div>
        
        <div className="filter-group">
          <select
            value={selectedProject}
            onChange={(e) => setSelectedProject(e.target.value)}
            className="form-control"
          >
            <option value="">All Projects</option>
            {projects.map(project => (
              <option key={project.id} value={project.id}>
                {project.name}
              </option>
            ))}
          </select>

          <select
            value={selectedStatus}
            onChange={(e) => setSelectedStatus(e.target.value)}
            className="form-control"
          >
            <option value="">All Statuses</option>
            <option value="TODO">To Do</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>

          <select
            value={selectedPriority}
            onChange={(e) => setSelectedPriority(e.target.value)}
            className="form-control"
          >
            <option value="">All Priorities</option>
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
            <option value="URGENT">Urgent</option>
          </select>
        </div>
      </div>

      {/* Task Form Modal */}
      {showForm && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>{editingTask ? 'Edit Task' : 'Create New Task'}</h2>
              <button 
                className="close-btn"
                onClick={resetForm}
              >
                ×
              </button>
            </div>
            
            <form onSubmit={handleSubmit} className="task-form">
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Task Title *</label>
                  <input
                    type="text"
                    className="form-control"
                    value={formData.title}
                    onChange={(e) => setFormData({...formData, title: e.target.value})}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Project *</label>
                  <select
                    className="form-control"
                    value={formData.projectId}
                    onChange={(e) => setFormData({...formData, projectId: e.target.value})}
                    required
                  >
                    <option value="">Select Project</option>
                    {projects.map(project => (
                      <option key={project.id} value={project.id}>
                        {project.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea
                  className="form-control"
                  rows={3}
                  value={formData.description}
                  onChange={(e) => setFormData({...formData, description: e.target.value})}
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Due Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.dueDate}
                    onChange={(e) => setFormData({...formData, dueDate: e.target.value})}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Estimated Hours</label>
                  <input
                    type="number"
                    className="form-control"
                    min="0"
                    step="0.5"
                    value={formData.estimatedHours || ''}
                    onChange={(e) => setFormData({...formData, estimatedHours: e.target.value ? parseFloat(e.target.value) : undefined})}
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Status</label>
                  <select
                    className="form-control"
                    value={formData.status}
                    onChange={(e) => setFormData({...formData, status: e.target.value as any})}
                  >
                    <option value="TODO">To Do</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="COMPLETED">Completed</option>
                    <option value="CANCELLED">Cancelled</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Priority</label>
                  <select
                    className="form-control"
                    value={formData.priority}
                    onChange={(e) => setFormData({...formData, priority: e.target.value as any})}
                  >
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                    <option value="URGENT">Urgent</option>
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Assignee</label>
                  <select
                    className="form-control"
                    value={formData.assigneeId}
                    onChange={(e) => setFormData({...formData, assigneeId: e.target.value})}
                  >
                    <option value="">Select Assignee</option>
                    {users.map(user => (
                      <option key={user.id} value={user.id}>
                        {user.firstName} {user.lastName}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Category</label>
                  <select
                    className="form-control"
                    value={formData.categoryId}
                    onChange={(e) => setFormData({...formData, categoryId: e.target.value})}
                  >
                    <option value="">Select Category</option>
                    {categories.map(category => (
                      <option key={category.id} value={category.id}>
                        {category.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={resetForm}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  {editingTask ? 'Update Task' : 'Create Task'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Kanban Board View */}
      <div className="kanban-board">
        {Object.entries(tasksByStatus).map(([status, statusTasks]) => (
          <div key={status} className="kanban-column">
            <div className="kanban-header">
              <h3>{status.replace('_', ' ')}</h3>
              <span className="task-count">{statusTasks.length}</span>
            </div>
            
            <div className="kanban-tasks">
              {statusTasks.map(task => (
                <div key={task.id} className={`task-card ${isOverdue(task.dueDate) ? 'overdue' : ''}`}>
                  <div className="task-card-header">
                    <h4>{task.title}</h4>
                    <div className="task-actions">
                      <button 
                        className="btn-icon"
                        onClick={() => handleEdit(task)}
                        title="Edit"
                      >
                        ✏️
                      </button>
                      <button 
                        className="btn-icon"
                        onClick={() => handleDelete(task.id)}
                        title="Delete"
                      >
                        🗑️
                      </button>
                    </div>
                  </div>
                  
                  <div className="task-card-body">
                    {task.description && (
                      <p className="task-description">{task.description}</p>
                    )}
                    
                    <div className="task-meta">
                      <div className="meta-item">
                        <strong>Project:</strong> {getProjectName(task.projectId)}
                      </div>
                      <div className="meta-item">
                        <strong>Assignee:</strong> {getAssigneeName(task.assigneeId)}
                      </div>
                      {task.categoryId && (
                        <div className="meta-item">
                          <strong>Category:</strong> {getCategoryName(task.categoryId)}
                        </div>
                      )}
                      {task.dueDate && (
                        <div className="meta-item">
                          <strong>Due:</strong> {new Date(task.dueDate).toLocaleDateString()}
                          {isOverdue(task.dueDate) && <span className="overdue-indicator">⚠️ Overdue</span>}
                        </div>
                      )}
                      {task.estimatedHours && (
                        <div className="meta-item">
                          <strong>Estimated:</strong> {task.estimatedHours}h
                          {task.actualHours && ` / Actual: ${task.actualHours}h`}
                        </div>
                      )}
                    </div>
                    
                    <div className="task-badges">
                      <span 
                        className="priority-badge" 
                        style={{ backgroundColor: getPriorityColor(task.priority) }}
                      >
                        {task.priority}
                      </span>
                    </div>
                    
                    {/* Quick Status Change */}
                    <div className="status-actions">
                      {task.status !== 'TODO' && (
                        <button 
                          className="btn-status"
                          onClick={() => handleStatusChange(task.id, 'TODO')}
                        >
                          ⬅️ To Do
                        </button>
                      )}
                      {task.status === 'TODO' && (
                        <button 
                          className="btn-status"
                          onClick={() => handleStatusChange(task.id, 'IN_PROGRESS')}
                        >
                          ▶️ Start
                        </button>
                      )}
                      {task.status === 'IN_PROGRESS' && (
                        <button 
                          className="btn-status"
                          onClick={() => handleStatusChange(task.id, 'COMPLETED')}
                        >
                          ✅ Complete
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))}
              
              {statusTasks.length === 0 && (
                <div className="empty-column">
                  <p>No tasks in this status</p>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default TaskManager;
