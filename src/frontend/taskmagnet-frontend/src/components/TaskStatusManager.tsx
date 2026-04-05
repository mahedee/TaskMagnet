import React, { useState, useEffect } from 'react';
import { taskStatusService } from '../services/taskStatusService';
import { projectService } from '../services/projectService';
import { TaskStatusResponse, TaskStatusRequest, ProjectResponse } from '../types';
import './TaskStatusManager.css';

const EMPTY_FORM = (projectId: number): TaskStatusRequest => ({ 
  name: '', 
  description: '', 
  color: '#6c757d',
  orderIndex: 1,
  isClosedStatus: false,
  projectId 
});

const TaskStatusManager: React.FC = () => {
  const [taskStatuses, setTaskStatuses] = useState<TaskStatusResponse[]>([]);
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingTaskStatus, setEditingTaskStatus] = useState<TaskStatusResponse | null>(null);
  const [formData, setFormData] = useState<TaskStatusRequest>(EMPTY_FORM(0));
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadProjects();
  }, []);

  useEffect(() => {
    if (selectedProjectId) {
      loadTaskStatuses();
    }
  }, [selectedProjectId]);

  const loadProjects = async () => {
    try {
      const projectsData = await projectService.getAll();
      setProjects(projectsData);
      if (projectsData.length > 0 && !selectedProjectId) {
        setSelectedProjectId(projectsData[0].id);
      }
    } catch (err) {
      console.error('Error loading projects:', err);
    }
  };

  const loadTaskStatuses = async () => {
    if (!selectedProjectId) return;
    
    try {
      setLoading(true);
      const statusData = await taskStatusService.getByProjectId(selectedProjectId);
      setTaskStatuses(statusData);
    } catch (err) {
      console.error('Error loading task statuses:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedProjectId) return;
    
    setError('');
    setSaving(true);
    try {
      const payload = { ...formData, projectId: selectedProjectId };
      
      if (editingTaskStatus) {
        await taskStatusService.update(editingTaskStatus.id, payload);
      } else {
        await taskStatusService.create(payload);
      }
      await loadTaskStatuses();
      resetForm();
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ||
        'Failed to save task status.';
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (status: TaskStatusResponse) => {
    setEditingTaskStatus(status);
    setFormData({
      name: status.name,
      description: status.description ?? '',
      color: status.color ?? '#6c757d',
      orderIndex: status.orderIndex,
      isClosedStatus: status.isClosedStatus ?? false,
      projectId: status.projectId
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Delete this task status? Any tasks using this status will need to be updated.')) return;
    try {
      await taskStatusService.delete(id);
      await loadTaskStatuses();
    } catch (err) {
      console.error('Error deleting task status:', err);
    }
  };

  const resetForm = () => {
    setFormData(EMPTY_FORM(selectedProjectId ?? 0));
    setEditingTaskStatus(null);
    setShowForm(false);
    setError('');
  };

  const filtered = taskStatuses.filter(ts =>
    ts.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (ts.description ?? '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getSelectedProjectName = () => {
    const project = projects.find(p => p.id === selectedProjectId);
    return project?.name || 'Unknown Project';
  };

  if (loading && taskStatuses.length === 0) {
    return (
      <div className="task-status-manager-loading">
        <div className="spinner"></div>
        <p>Loading task statuses…</p>
      </div>
    );
  }

  return (
    <div className="task-status-manager">
      <header className="task-status-manager-header">
        <div>
          <h1>Task Statuses</h1>
          <p className="task-status-subtitle">Manage custom task statuses for your projects</p>
        </div>
        <button 
          className="btn btn-primary" 
          onClick={() => setShowForm(true)}
          disabled={!selectedProjectId}
        >
          + New Status
        </button>
      </header>

      {/* Project Selection */}
      <div className="task-status-toolbar">
        <div className="project-selector">
          <label className="form-label">Project:</label>
          <select
            className="form-control project-select"
            value={selectedProjectId ?? ''}
            onChange={(e) => setSelectedProjectId(Number(e.target.value))}
          >
            <option value="">Select a project...</option>
            {projects.map(project => (
              <option key={project.id} value={project.id}>
                {project.name} [{project.code}]
              </option>
            ))}
          </select>
        </div>

        {selectedProjectId && (
          <>
            <input
              type="text"
              className="form-control search-input"
              placeholder="Search task statuses…"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <span className="task-status-count">
              {filtered.length} status{filtered.length === 1 ? '' : 'es'} in {getSelectedProjectName()}
            </span>
          </>
        )}
      </div>

      {/* Modal */}
      {showForm && (
        <div className="modal-overlay">
          <div className="modal modal--sm">
            <div className="modal-header">
              <h2>{editingTaskStatus ? 'Edit Task Status' : 'New Task Status'}</h2>
              <button className="close-btn" onClick={resetForm}>×</button>
            </div>
            <form onSubmit={handleSubmit} className="task-status-form">
              {error && <div className="form-error">{error}</div>}
              <div className="form-group">
                <label className="form-label">Name *</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. In Review"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  autoFocus
                />
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea
                  className="form-control"
                  rows={3}
                  placeholder="Optional description…"
                  value={formData.description ?? ''}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Color</label>
                  <input
                    type="color"
                    className="form-control color-input"
                    value={formData.color ?? '#6c757d'}
                    onChange={(e) => setFormData({ ...formData, color: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Order</label>
                  <input
                    type="number"
                    className="form-control"
                    min="1"
                    value={formData.orderIndex ?? 1}
                    onChange={(e) => setFormData({ ...formData, orderIndex: Number(e.target.value) })}
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-checkbox">
                  <input
                    type="checkbox"
                    checked={formData.isClosedStatus ?? false}
                    onChange={(e) => setFormData({ ...formData, isClosedStatus: e.target.checked })}
                  />
                  <span className="checkmark"></span>
                  Closed status (tasks with this status are considered complete)
                </label>
              </div>
              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={resetForm}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Saving…' : editingTaskStatus ? 'Update' : 'Create'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Content */}
      {!selectedProjectId ? (
        <div className="task-status-empty">
          <span className="empty-icon">📋</span>
          <h3>Select a project to manage task statuses</h3>
          <p>Choose a project from the dropdown above to view and manage its task statuses.</p>
        </div>
      ) : filtered.length === 0 ? (
        <div className="task-status-empty">
          {searchTerm ? (
            <>
              <span className="empty-icon">🔍</span>
              <h3>No task statuses match "{searchTerm}"</h3>
              <p>Try a different search term.</p>
            </>
          ) : (
            <>
              <span className="empty-icon">📋</span>
              <h3>No task statuses yet</h3>
              <p>Create your first task status for {getSelectedProjectName()}.</p>
              <button className="btn btn-primary" onClick={() => setShowForm(true)}>
                + New Status
              </button>
            </>
          )}
        </div>
      ) : (
        <div className="task-status-list">
          <div className="task-status-list-header">
            <div className="task-status-list-col task-status-list-col--icon"></div>
            <div className="task-status-list-col task-status-list-col--name">Name</div>
            <div className="task-status-list-col task-status-list-col--description">Description</div>
            <div className="task-status-list-col task-status-list-col--color">Color</div>
            <div className="task-status-list-col task-status-list-col--order">Order</div>
            <div className="task-status-list-col task-status-list-col--type">Type</div>
            <div className="task-status-list-col task-status-list-col--actions">Actions</div>
          </div>
          {filtered.map(status => (
            <div key={status.id} className="task-status-list-item">
              <div className="task-status-list-col task-status-list-col--icon">
                <span className="task-status-icon">📋</span>
              </div>
              <div className="task-status-list-col task-status-list-col--name">
                <h3 className="task-status-name">{status.name}</h3>
              </div>
              <div className="task-status-list-col task-status-list-col--description">
                <p className="task-status-description">
                  {status.description || <span className="text-muted">No description</span>}
                </p>
              </div>
              <div className="task-status-list-col task-status-list-col--color">
                <div className="color-indicator">
                  <div 
                    className="color-swatch" 
                    style={{ backgroundColor: status.color ?? '#6c757d' }}
                  ></div>
                  <span className="color-code">{status.color ?? '#6c757d'}</span>
                </div>
              </div>
              <div className="task-status-list-col task-status-list-col--order">
                <span className="order-badge">{status.orderIndex ?? 1}</span>
              </div>
              <div className="task-status-list-col task-status-list-col--type">
                <span className={`type-badge ${status.isClosedStatus ? 'closed' : 'open'}`}>
                  {status.isClosedStatus ? 'Closed' : 'Open'}
                </span>
              </div>
              <div className="task-status-list-col task-status-list-col--actions">
                <button className="btn-icon" onClick={() => handleEdit(status)} title="Edit">✏️</button>
                <button className="btn-icon btn-icon--danger" onClick={() => handleDelete(status.id)} title="Delete">🗑️</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default TaskStatusManager;