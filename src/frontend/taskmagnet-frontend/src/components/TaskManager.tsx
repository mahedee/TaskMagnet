import React, { useState, useEffect } from 'react';
import { taskService } from '../services/taskService';
import { projectService } from '../services/projectService';
import { userService } from '../services/userService';
import { categoryService } from '../services/categoryService';
import { useAuth } from '../contexts/AuthContext';
import { TaskResponse, ProjectResponse, UserResponse, CategoryResponse, TaskRequest, TaskStatus, Priority } from '../types';
import './TaskManager.css';

const STATUS_COLORS: Record<string, string> = {
  NOT_STARTED: '#95a5a6',
  IN_PROGRESS: '#3498db',
  IN_REVIEW: '#8e44ad',
  ON_HOLD: '#f39c12',
  COMPLETED: '#27ae60',
  APPROVED: '#1abc9c',
  REJECTED: '#e74c3c',
  CANCELLED: '#7f8c8d',
};

const PRIORITY_COLORS: Record<string, string> = {
  CRITICAL: '#c0392b',
  URGENT: '#e74c3c',
  HIGH: '#f39c12',
  MEDIUM: '#3498db',
  LOW: '#27ae60',
};

const KANBAN_COLUMNS: TaskStatus[] = ['NOT_STARTED', 'IN_PROGRESS', 'IN_REVIEW', 'ON_HOLD', 'COMPLETED'];

const EMPTY_FORM = (createdById: number): TaskRequest => ({
  title: '',
  description: '',
  dueDate: '',
  status: 'NOT_STARTED',
  priority: 'MEDIUM',
  projectId: undefined,
  assignedToId: undefined,
  categoryId: undefined,
  estimatedHours: undefined,
  createdById,
});

const TaskManager: React.FC = () => {
  const { user } = useAuth();
  const [tasks, setTasks] = useState<TaskResponse[]>([]);
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingTask, setEditingTask] = useState<TaskResponse | null>(null);
  const [selectedProject, setSelectedProject] = useState<string>('');
  const [selectedStatus, setSelectedStatus] = useState<string>('');
  const [selectedPriority, setSelectedPriority] = useState<string>('');
  const [searchTerm, setSearchTerm] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [selectedTaskForDetails, setSelectedTaskForDetails] = useState<TaskResponse | null>(null);

  // Form state
  const [formData, setFormData] = useState<TaskRequest>(EMPTY_FORM(0));

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [tasksData, projectsData, usersData, categoriesData] = await Promise.all([
        taskService.getAll(),
        projectService.getAll(),
        userService.getAll(),
        categoryService.getAll(),
      ]);
      setTasks(tasksData);
      setProjects(projectsData);
      setUsers(usersData);
      setCategories(categoriesData);
    } catch (err) {
      console.error('Error loading data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSaving(true);
    try {
      const payload: TaskRequest = {
        ...formData,
        createdById: user!.id,
        projectId: formData.projectId || undefined,
        assignedToId: formData.assignedToId || undefined,
        categoryId: formData.categoryId || undefined,
      };
      if (editingTask) {
        await taskService.update(editingTask.id, payload);
      } else {
        await taskService.create(payload);
      }
      await loadData();
      resetForm();
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ||
        'Failed to save task.';
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (task: TaskResponse) => {
    setEditingTask(task);
    setFormData({
      title: task.title,
      description: task.description ?? '',
      dueDate: task.dueDate ?? '',
      status: task.status,
      priority: task.priority,
      projectId: task.projectId,
      assignedToId: task.assignedToId,
      categoryId: task.categoryId,
      estimatedHours: task.estimatedHours,
      createdById: task.createdById,
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this task?')) return;
    try {
      await taskService.delete(id);
      await loadData();
    } catch (err) {
      console.error('Error deleting task:', err);
    }
  };

  const handleStatusChange = async (taskId: number, newStatus: TaskStatus) => {
    try {
      const task = tasks.find(t => t.id === taskId);
      if (task) {
        await taskService.update(taskId, {
          title: task.title,
          status: newStatus,
          priority: task.priority,
          createdById: task.createdById,
          projectId: task.projectId,
          assignedToId: task.assignedToId,
        });
        await loadData();
      }
    } catch (err) {
      console.error('Error updating task status:', err);
    }
  };

  const resetForm = () => {
    setFormData(EMPTY_FORM(user?.id ?? 0));
    setEditingTask(null);
    setShowForm(false);
    setError('');
  };

  const handleShowDetails = (task: TaskResponse) => {
    setSelectedTaskForDetails(task);
    setShowDetailsModal(true);
  };

  const handleCloseDetails = () => {
    setSelectedTaskForDetails(null);
    setShowDetailsModal(false);
  };

  const truncateText = (text: string, maxLength: number = 200): string => {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  };

  const getStatusColor = (status: string): string => {
    return STATUS_COLORS[status] ?? '#95a5a6';
  };

  const getPriorityColor = (priority: string): string => {
    return PRIORITY_COLORS[priority] ?? '#95a5a6';
  };

  // Filter tasks based on search and filters
  const getProjectName = (projectId?: number): string => {
    if (!projectId) return 'No Project';
    return projects.find(p => p.id === projectId)?.name ?? 'Unknown Project';
  };

  const getAssigneeName = (assignedToId?: number): string => {
    if (!assignedToId) return 'Unassigned';
    const u = users.find(u => u.id === assignedToId);
    return u ? (u.fullName || `${u.firstName} ${u.lastName}`) : 'Unknown User';
  };

  const isOverdue = (task: TaskResponse): boolean => {
    if (!task.dueDate) return false;
    return new Date(task.dueDate) < new Date() && task.status !== 'COMPLETED' && task.status !== 'CANCELLED';
  };

  const filteredTasks = tasks.filter(task => {
    const matchesSearch = task.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         (task.description?.toLowerCase() ?? '').includes(searchTerm.toLowerCase());
    const matchesProject = !selectedProject || String(task.projectId) === selectedProject;
    const matchesStatus = !selectedStatus || task.status === selectedStatus;
    const matchesPriority = !selectedPriority || task.priority === selectedPriority;
    return matchesSearch && matchesProject && matchesStatus && matchesPriority;
  });

  // No need to group by status for grid view
  // const tasksByStatus = KANBAN_COLUMNS.reduce<Record<string, TaskResponse[]>>((acc, status) => {
  //   acc[status] = filteredTasks.filter(t => t.status === status);
  //   return acc;
  // }, {});

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
        <button className="btn btn-primary" onClick={() => setShowForm(true)}>
          + New Task
        </button>
      </header>

      {/* Filters */}
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
          <select value={selectedProject} onChange={(e) => setSelectedProject(e.target.value)} className="form-control">
            <option value="">All Projects</option>
            {projects.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
          </select>
          <select value={selectedStatus} onChange={(e) => setSelectedStatus(e.target.value)} className="form-control">
            <option value="">All Statuses</option>
            <option value="NOT_STARTED">Not Started</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="IN_REVIEW">In Review</option>
            <option value="ON_HOLD">On Hold</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
          <select value={selectedPriority} onChange={(e) => setSelectedPriority(e.target.value)} className="form-control">
            <option value="">All Priorities</option>
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
            <option value="URGENT">Urgent</option>
            <option value="CRITICAL">Critical</option>
          </select>
        </div>
      </div>

      {/* Task Form Modal */}
      {showForm && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>{editingTask ? 'Edit Task' : 'Create New Task'}</h2>
              <button className="close-btn" onClick={resetForm}>×</button>
            </div>
            <form onSubmit={handleSubmit} className="task-form">
              {error && <div className="form-error">{error}</div>}
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
                  <label className="form-label">Project</label>
                  <select
                    className="form-control"
                    value={formData.projectId ?? ''}
                    onChange={(e) => setFormData({...formData, projectId: e.target.value ? Number(e.target.value) : undefined})}
                  >
                    <option value="">No Project</option>
                    {projects.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
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
                    value={formData.estimatedHours ?? ''}
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
                    onChange={(e) => setFormData({...formData, status: e.target.value as TaskStatus})}
                  >
                    <option value="NOT_STARTED">Not Started</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="IN_REVIEW">In Review</option>
                    <option value="ON_HOLD">On Hold</option>
                    <option value="COMPLETED">Completed</option>
                    <option value="CANCELLED">Cancelled</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Priority</label>
                  <select
                    className="form-control"
                    value={formData.priority}
                    onChange={(e) => setFormData({...formData, priority: e.target.value as Priority})}
                  >
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                    <option value="URGENT">Urgent</option>
                    <option value="CRITICAL">Critical</option>
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Assignee</label>
                  <select
                    className="form-control"
                    value={formData.assignedToId ?? ''}
                    onChange={(e) => setFormData({...formData, assignedToId: e.target.value ? Number(e.target.value) : undefined})}
                  >
                    <option value="">Unassigned</option>
                    {users.map(u => (
                      <option key={u.id} value={u.id}>
                        {u.fullName || `${u.firstName} ${u.lastName}`}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Category</label>
                  <select
                    className="form-control"
                    value={formData.categoryId ?? ''}
                    onChange={(e) => setFormData({...formData, categoryId: e.target.value ? Number(e.target.value) : undefined})}
                  >
                    <option value="">No Category</option>
                    {categories.map(c => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={resetForm}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Saving...' : editingTask ? 'Update Task' : 'Create Task'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* List View */}
      <div className="task-list-container">
        <div className="task-list-header">
          <h2>All Tasks ({filteredTasks.length})</h2>
        </div>
        
        {filteredTasks.length === 0 ? (
          <div className="empty-list">
            <p>No tasks found matching your filters.</p>
          </div>
        ) : (
          <div className="task-table">
            <div className="task-table-header">
              <div className="col-id">Task ID</div>
              <div className="col-summary">Task Summary</div>
              <div className="col-status">Status</div>
              <div className="col-assignee">Assignee</div>
              <div className="col-due">Due Date</div>
              <div className="col-details">Details</div>
              <div className="col-edit">Edit</div>
              <div className="col-delete">Delete</div>
            </div>
            <div className="task-table-body">
              {filteredTasks.map(task => (
                <div key={task.id} className={`task-row${isOverdue(task) ? ' overdue' : ''}`}>
                  <div className="col-id">
                    #{task.id}
                  </div>
                  <div className="col-summary">
                    <div className="task-summary">
                      <div className="status-indicator" style={{ backgroundColor: getStatusColor(task.status) }}></div>
                      <div className="summary-content">
                        <h4 title={task.title}>{truncateText(task.title)}</h4>
                        {task.description && (
                          <p className="task-description" title={task.description}>
                            {truncateText(task.description, 100)}
                          </p>
                        )}
                      </div>
                    </div>
                  </div>
                  <div className="col-status">
                    <span className="status-badge" style={{ backgroundColor: getStatusColor(task.status) }}>
                      {task.status.replace(/_/g, ' ')}
                    </span>
                  </div>
                  <div className="col-assignee">{getAssigneeName(task.assignedToId)}</div>
                  <div className="col-due">
                    {task.dueDate ? (
                      <>
                        {new Date(task.dueDate).toLocaleDateString()}
                        {isOverdue(task) && <span className="overdue-indicator"> ⚠️</span>}
                      </>
                    ) : (
                      <span className="no-date">-</span>
                    )}
                  </div>
                  <div className="col-details">
                    <button 
                      className="btn-details" 
                      onClick={() => handleShowDetails(task)} 
                      title="View Details"
                    >
                      📋
                    </button>
                  </div>
                  <div className="col-edit">
                    <button className="btn-edit" onClick={() => handleEdit(task)} title="Edit Task">
                      ✏️
                    </button>
                  </div>
                  <div className="col-delete">
                    <button className="btn-delete" onClick={() => handleDelete(task.id)} title="Delete Task">
                      🗑️
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Task Details Modal */}
      {showDetailsModal && selectedTaskForDetails && (
        <div className="modal-overlay">
          <div className="modal details-modal">
            <div className="modal-header">
              <h2>📋 Task Details</h2>
              <button className="close-btn" onClick={handleCloseDetails}>×</button>
            </div>
            <div className="task-details">
              <div className="details-grid">
                <div className="detail-section">
                  <div className="detail-item">
                    <span className="detail-label">🆔 Task ID:</span>
                    <span className="detail-value">#{selectedTaskForDetails.id}</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">📝 Title:</span>
                    <span className="detail-value">{selectedTaskForDetails.title}</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">📄 Description:</span>
                    <span className="detail-value">
                      {selectedTaskForDetails.description || 'No description provided'}
                    </span>
                  </div>
                </div>
                
                <div className="detail-section">
                  <div className="detail-item">
                    <span className="detail-label">📊 Status:</span>
                    <span className="detail-value">
                      <span className="status-badge" style={{ backgroundColor: getStatusColor(selectedTaskForDetails.status) }}>
                        {selectedTaskForDetails.status.replace(/_/g, ' ')}
                      </span>
                    </span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">⚡ Priority:</span>
                    <span className="detail-value">
                      <span className="priority-badge" style={{ backgroundColor: PRIORITY_COLORS[selectedTaskForDetails.priority] ?? '#95a5a6' }}>
                        {selectedTaskForDetails.priority}
                      </span>
                    </span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">💼 Project:</span>
                    <span className="detail-value">{getProjectName(selectedTaskForDetails.projectId)}</span>
                  </div>
                </div>
                
                <div className="detail-section">
                  <div className="detail-item">
                    <span className="detail-label">👤 Assigned To:</span>
                    <span className="detail-value">{getAssigneeName(selectedTaskForDetails.assignedToId)}</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">👨‍💼 Created By:</span>
                    <span className="detail-value">{getAssigneeName(selectedTaskForDetails.createdById)}</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">📅 Due Date:</span>
                    <span className="detail-value">
                      {selectedTaskForDetails.dueDate ? (
                        <>
                          {new Date(selectedTaskForDetails.dueDate).toLocaleDateString()}
                          {isOverdue(selectedTaskForDetails) && <span className="overdue-indicator"> ⚠️ Overdue</span>}
                        </>
                      ) : (
                        'No due date set'
                      )}
                    </span>
                  </div>
                </div>
                
                <div className="detail-section">
                  <div className="detail-item">
                    <span className="detail-label">⏰ Estimated Hours:</span>
                    <span className="detail-value">
                      {selectedTaskForDetails.estimatedHours ? `${selectedTaskForDetails.estimatedHours} hours` : 'Not specified'}
                    </span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">⏱️ Actual Hours:</span>
                    <span className="detail-value">
                      {selectedTaskForDetails.actualHours ? `${selectedTaskForDetails.actualHours} hours` : 'Not tracked yet'}
                    </span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">🏷️ Category:</span>
                    <span className="detail-value">
                      {selectedTaskForDetails.categoryId && categories.length > 0
                        ? categories.find(c => c.id === selectedTaskForDetails.categoryId)?.name || 'Unknown Category'
                        : 'No category assigned'
                      }
                    </span>
                  </div>
                </div>
              </div>
              
              <div className="details-actions">
                <button className="btn btn-primary" onClick={() => {
                  handleEdit(selectedTaskForDetails);
                  handleCloseDetails();
                }}>
                  ✏️ Edit Task
                </button>
                <button className="btn btn-secondary" onClick={handleCloseDetails}>
                  Close
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TaskManager;
