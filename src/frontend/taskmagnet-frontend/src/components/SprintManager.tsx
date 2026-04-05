import React, { useState, useEffect } from 'react';
import { sprintService } from '../services/sprintService';
import { projectService } from '../services/projectService';
import { useAuth } from '../contexts/AuthContext';
import { 
  SprintResponse, 
  SprintRequest, 
  ProjectResponse, 
  TaskResponse, 
  SprintStatus 
} from '../types';
import './SprintManager.css';

const STATUS_COLORS: Record<SprintStatus, string> = {
  PLANNED: '#95a5a6',
  ACTIVE: '#3498db',
  COMPLETED: '#27ae60',
  CANCELLED: '#e74c3c',
};

const EMPTY_FORM: SprintRequest = {
  name: '',
  description: '',
  startDate: '',
  endDate: '',
  goals: '',
  projectId: 0,
};

const SprintManager: React.FC = () => {
  const { user } = useAuth();
  const [sprints, setSprints] = useState<SprintResponse[]>([]);
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingSprint, setEditingSprint] = useState<SprintResponse | null>(null);
  const [selectedProject, setSelectedProject] = useState<string>('');
  const [selectedStatus, setSelectedStatus] = useState<string>('');
  const [searchTerm, setSearchTerm] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [selectedSprintForDetails, setSelectedSprintForDetails] = useState<SprintResponse | null>(null);
  const [sprintTasks, setSprintTasks] = useState<TaskResponse[]>([]);

  // Form state
  const [formData, setFormData] = useState<SprintRequest>(EMPTY_FORM);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [sprintsData, projectsData] = await Promise.all([
        sprintService.getAll(),
        projectService.getAll(),
      ]);
      setSprints(sprintsData);
      setProjects(projectsData);
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
      if (editingSprint) {
        await sprintService.update(editingSprint.id, formData);
      } else {
        await sprintService.create(formData);
      }
      await loadData();
      resetForm();
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Failed to save sprint.';
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (sprint: SprintResponse) => {
    setEditingSprint(sprint);
    setFormData({
      name: sprint.name,
      description: sprint.description || '',
      startDate: sprint.startDate,
      endDate: sprint.endDate,
      goals: sprint.goals || '',
      projectId: sprint.projectId,
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this sprint?')) return;
    try {
      await sprintService.delete(id);
      await loadData();
    } catch (err) {
      console.error('Error deleting sprint:', err);
    }
  };

  const handleStart = async (id: number) => {
    if (!window.confirm('Are you sure you want to start this sprint?')) return;
    try {
      await sprintService.start(id);
      await loadData();
    } catch (err) {
      console.error('Error starting sprint:', err);
    }
  };

  const handleComplete = async (id: number) => {
    if (!window.confirm('Are you sure you want to complete this sprint?')) return;
    try {
      await sprintService.complete(id);
      await loadData();
    } catch (err) {
      console.error('Error completing sprint:', err);
    }
  };

  const handleShowDetails = async (sprint: SprintResponse) => {
    setSelectedSprintForDetails(sprint);
    try {
      const tasks = await sprintService.getTasks(sprint.id);
      setSprintTasks(tasks);
      setShowDetailsModal(true);
    } catch (err) {
      console.error('Error loading sprint tasks:', err);
      setSprintTasks([]);
      setShowDetailsModal(true);
    }
  };

  const resetForm = () => {
    setFormData(EMPTY_FORM);
    setEditingSprint(null);
    setShowForm(false);
    setError('');
  };

  const getStatusColor = (status: SprintStatus) => STATUS_COLORS[status] || '#6c757d';

  const getProjectName = (projectId?: number) => {
    const project = projects.find(p => p.id === projectId);
    return project?.name || 'Unknown';
  };

  const truncateText = (text: string, maxLength: number = 200): string => {
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
  };

  const filteredSprints = sprints.filter(sprint => {
    const matchesSearch = sprint.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         (sprint.description || '').toLowerCase().includes(searchTerm.toLowerCase());
    const matchesProject = selectedProject === '' || sprint.projectId.toString() === selectedProject;
    const matchesStatus = selectedStatus === '' || sprint.status === selectedStatus;
    return matchesSearch && matchesProject && matchesStatus;
  });

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  const getDuration = (sprint: SprintResponse) => {
    const start = new Date(sprint.startDate);
    const end = new Date(sprint.endDate);
    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return `${diffDays} days`;
  };

  if (loading) {
    return (
      <div className="sprint-manager-loading">
        <div className="spinner"></div>
        <p>Loading sprints...</p>
      </div>
    );
  }

  return (
    <div className="sprint-manager">
      <header className="sprint-manager-header">
        <h1>Sprint Planning</h1>
        <button className="btn btn-primary" onClick={() => setShowForm(true)}>
          + New Sprint
        </button>
      </header>

      {/* Filters */}
      <div className="sprint-filters">
        <div className="search-box">
          <input
            type="text"
            placeholder="Search sprints..."
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
              <option key={project.id} value={project.id.toString()}>{project.name}</option>
            ))}
          </select>
          <select
            value={selectedStatus}
            onChange={(e) => setSelectedStatus(e.target.value)}
            className="form-control"
          >
            <option value="">All Statuses</option>
            <option value="PLANNED">Planned</option>
            <option value="ACTIVE">Active</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </div>
      </div>

      {/* Create/Edit Form Modal */}
      {showForm && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>{editingSprint ? 'Edit Sprint' : 'Create New Sprint'}</h2>
              <button className="close-btn" onClick={resetForm}>×</button>
            </div>
            <form onSubmit={handleSubmit} className="sprint-form">
              {error && <div className="form-error">{error}</div>}
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Sprint Name *</label>
                  <input
                    type="text"
                    className="form-control"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Project *</label>
                  <select
                    className="form-control"
                    value={formData.projectId || ''}
                    onChange={(e) => setFormData({ ...formData, projectId: Number(e.target.value) })}
                    required
                  >
                    <option value="">Select Project</option>
                    {projects.map(project => (
                      <option key={project.id} value={project.id}>{project.name}</option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Start Date *</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.startDate}
                    onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">End Date *</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.endDate}
                    onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                    required
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea
                  className="form-control"
                  rows={3}
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Sprint Goals</label>
                <textarea
                  className="form-control"
                  rows={3}
                  value={formData.goals}
                  onChange={(e) => setFormData({ ...formData, goals: e.target.value })}
                />
              </div>
              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={resetForm}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Saving...' : editingSprint ? 'Update Sprint' : 'Create Sprint'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Sprint List */}
      <div className="sprints-list">
        {filteredSprints.length === 0 ? (
          <div className="no-sprints">
            <span className="empty-icon">🏃</span>
            <h3>No sprints found</h3>
            <p>Create your first sprint to start planning!</p>
            <button className="btn btn-primary" onClick={() => setShowForm(true)}>
              + New Sprint
            </button>
          </div>
        ) : (
          <div className="sprint-list">
            <div className="sprint-list-header">
              <div className="sprint-list-col sprint-list-col--icon"></div>
              <div className="sprint-list-col sprint-list-col--name">Sprint</div>
              <div className="sprint-list-col sprint-list-col--project">Project</div>
              <div className="sprint-list-col sprint-list-col--status">Status</div>
              <div className="sprint-list-col sprint-list-col--dates">Timeline</div>
              <div className="sprint-list-col sprint-list-col--progress">Progress</div>
              <div className="sprint-list-col sprint-list-col--actions">Actions</div>
            </div>
            {filteredSprints.map(sprint => (
              <div key={sprint.id} className="sprint-list-item">
                <div className="sprint-list-col sprint-list-col--icon">
                  <span className="sprint-icon">🏃</span>
                </div>
                <div className="sprint-list-col sprint-list-col--name">
                  <div className="sprint-name-info">
                    <h3 className="sprint-name">{sprint.name}</h3>
                    <span className="sprint-meta">#{sprint.id} · {getDuration(sprint)}</span>
                    {sprint.description && (
                      <p className="sprint-description">{truncateText(sprint.description, 80)}</p>
                    )}
                  </div>
                </div>
                <div className="sprint-list-col sprint-list-col--project">
                  <span className="sprint-project">{getProjectName(sprint.projectId)}</span>
                </div>
                <div className="sprint-list-col sprint-list-col--status">
                  <span
                    className="status-badge"
                    style={{ backgroundColor: getStatusColor(sprint.status) }}
                  >
                    {sprint.status.replace(/_/g, ' ')}
                  </span>
                </div>
                <div className="sprint-list-col sprint-list-col--dates">
                  <div className="sprint-timeline">
                    <div className="timeline-item">
                      <small>Start: {formatDate(sprint.startDate)}</small>
                    </div>
                    <div className="timeline-item">
                      <small>End: {formatDate(sprint.endDate)}</small>
                    </div>
                  </div>
                </div>
                <div className="sprint-list-col sprint-list-col--progress">
                  <div className="progress-info">
                    <div className="progress-bar">
                      <div
                        className="progress-fill"
                        style={{ width: `${sprint.completionPercentage || 0}%` }}
                      ></div>
                    </div>
                    <small>{sprint.completionPercentage || 0}%</small>
                  </div>
                </div>
                <div className="sprint-list-col sprint-list-col--actions">
                  <button
                    className="btn-icon"
                    onClick={() => handleShowDetails(sprint)}
                    title="View Details"
                  >
                    📋
                  </button>
                  <button
                    className="btn-icon"
                    onClick={() => handleEdit(sprint)}
                    title="Edit Sprint"
                  >
                    ✏️
                  </button>
                  {sprint.status === 'PLANNED' && (
                    <button
                      className="btn-icon btn-icon--success"
                      onClick={() => handleStart(sprint.id)}
                      title="Start Sprint"
                    >
                      ▶
                    </button>
                  )}
                  {sprint.status === 'ACTIVE' && (
                    <button
                      className="btn-icon btn-icon--success"
                      onClick={() => handleComplete(sprint.id)}
                      title="Complete Sprint"
                    >
                      ✔
                    </button>
                  )}
                  <button
                    className="btn-icon btn-icon--danger"
                    onClick={() => handleDelete(sprint.id)}
                    title="Delete Sprint"
                  >
                    🗑️
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Details Modal */}
      {showDetailsModal && selectedSprintForDetails && (
        <div className="modal-overlay" onClick={() => setShowDetailsModal(false)}>
          <div className="modal modal-large" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Sprint Details</h2>
              <button className="close-btn" onClick={() => setShowDetailsModal(false)}>×</button>
            </div>
            <div className="sprint-details">
              <div className="detail-section">
                <h3>{selectedSprintForDetails.name}</h3>
                <div className="detail-grid">
                  <div className="detail-cell">
                    <span className="detail-label">Status</span>
                    <span
                      className="status-badge"
                      style={{ backgroundColor: getStatusColor(selectedSprintForDetails.status) }}
                    >
                      {selectedSprintForDetails.status.replace(/_/g, ' ')}
                    </span>
                  </div>
                  <div className="detail-cell">
                    <span className="detail-label">Project</span>
                    <span className="detail-value">{getProjectName(selectedSprintForDetails.projectId)}</span>
                  </div>
                  <div className="detail-cell">
                    <span className="detail-label">Duration</span>
                    <span className="detail-value">{getDuration(selectedSprintForDetails)}</span>
                  </div>
                  <div className="detail-cell">
                    <span className="detail-label">Progress</span>
                    <span className="detail-value">{selectedSprintForDetails.completionPercentage || 0}%</span>
                  </div>
                  <div className="detail-cell">
                    <span className="detail-label">Start Date</span>
                    <span className="detail-value">{formatDate(selectedSprintForDetails.startDate)}</span>
                  </div>
                  <div className="detail-cell">
                    <span className="detail-label">End Date</span>
                    <span className="detail-value">{formatDate(selectedSprintForDetails.endDate)}</span>
                  </div>
                  {selectedSprintForDetails.actualStartDate && (
                    <div className="detail-cell">
                      <span className="detail-label">Actual Start</span>
                      <span className="detail-value">{formatDate(selectedSprintForDetails.actualStartDate)}</span>
                    </div>
                  )}
                  {selectedSprintForDetails.actualEndDate && (
                    <div className="detail-cell">
                      <span className="detail-label">Actual End</span>
                      <span className="detail-value">{formatDate(selectedSprintForDetails.actualEndDate)}</span>
                    </div>
                  )}
                </div>
                {selectedSprintForDetails.description && (
                  <div className="detail-item">
                    <strong>Description</strong>
                    <p>{selectedSprintForDetails.description}</p>
                  </div>
                )}
                {selectedSprintForDetails.goals && (
                  <div className="detail-item">
                    <strong>Sprint Goals</strong>
                    <p>{selectedSprintForDetails.goals}</p>
                  </div>
                )}
              </div>
              <div className="detail-section">
                <h3>Sprint Tasks ({sprintTasks.length})</h3>
                {sprintTasks.length > 0 ? (
                  <div className="tasks-list">
                    {sprintTasks.map(task => (
                      <div key={task.id} className="task-item">
                        <span className="task-id">#{task.id}</span>
                        <span className="task-title">{task.title}</span>
                        <span className="task-status">{task.status.replace(/_/g, ' ')}</span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="no-tasks">No tasks assigned to this sprint yet.</p>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SprintManager;
