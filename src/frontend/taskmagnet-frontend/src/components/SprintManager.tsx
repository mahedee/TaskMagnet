import React, { useState, useEffect } from 'react';
import { sprintService } from '../services/sprintService';
import { projectService } from '../services/projectService';
import { taskService } from '../services/taskService';
import { useAuth } from '../contexts/AuthContext';
import { 
  SprintResponse, 
  SprintRequest, 
  ProjectResponse, 
  TaskResponse, 
  SprintStatus 
} from '../types';
// import './SprintManager.css';

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
  const [availableTasks, setAvailableTasks] = useState<TaskResponse[]>([]);

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
        <div className="loading-spinner"></div>
        <p>Loading sprints...</p>
      </div>
    );
  }

  return (
    <div className="sprint-manager">
      <div className="sprint-manager-header">
        <h1>Sprint Planning</h1>
        <button className="btn btn-primary" onClick={() => setShowForm(true)}>
          + Create Sprint
        </button>
      </div>

      {/* Filters */}
      <div className="sprint-filters">
        <div className="search-box">
          <input
            type="text"
            placeholder="Search sprints..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="form-input"
          />
        </div>
        <select 
          value={selectedProject} 
          onChange={(e) => setSelectedProject(e.target.value)}
          className="form-select"
        >
          <option value="">All Projects</option>
          {projects.map(project => (
            <option key={project.id} value={project.id.toString()}>
              {project.name}
            </option>
          ))}
        </select>
        <select 
          value={selectedStatus} 
          onChange={(e) => setSelectedStatus(e.target.value)}
          className="form-select"
        >
          <option value="">All Statuses</option>
          <option value="PLANNED">Planned</option>
          <option value="ACTIVE">Active</option>
          <option value="COMPLETED">Completed</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
      </div>

      {/* Sprint Table */}
      <div className="sprint-table">
        <div className="sprint-table-header">
          <div className="col-id">Sprint ID</div>
          <div className="col-name">Sprint Name</div>
          <div className="col-status">Status</div>
          <div className="col-project">Project</div>
          <div className="col-duration">Duration</div>
          <div className="col-progress">Progress</div>
          <div className="col-actions">Actions</div>
        </div>
        <div className="sprint-table-body">
          {filteredSprints.map(sprint => (
            <div key={sprint.id} className="sprint-row">
              <div className="col-id">
                #{sprint.id}
              </div>
              <div className="col-name">
                <div className="sprint-name">
                  <div className="status-indicator" style={{ backgroundColor: getStatusColor(sprint.status) }}></div>
                  <div className="name-content">
                    <h4 title={sprint.name}>{truncateText(sprint.name, 50)}</h4>
                    {sprint.description && (
                      <p className="sprint-description" title={sprint.description}>
                        {truncateText(sprint.description, 80)}
                      </p>
                    )}
                  </div>
                </div>
              </div>
              <div className="col-status">
                <span className="status-badge" style={{ backgroundColor: getStatusColor(sprint.status) }}>
                  {sprint.status}
                </span>
              </div>
              <div className="col-project">{getProjectName(sprint.projectId)}</div>
              <div className="col-duration">
                <div className="duration-info">
                  <div>{getDuration(sprint)}</div>
                  <small>{formatDate(sprint.startDate)} - {formatDate(sprint.endDate)}</small>
                </div>
              </div>
              <div className="col-progress">
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
              <div className="col-actions">
                <div className="action-buttons">
                  <button 
                    className="btn-details" 
                    onClick={() => handleShowDetails(sprint)} 
                    title="View Details"
                  >
                    📋
                  </button>
                  <button className="btn-edit" onClick={() => handleEdit(sprint)} title="Edit Sprint">
                    ✏️
                  </button>
                  {sprint.status === 'PLANNED' && (
                    <button className="btn-start" onClick={() => handleStart(sprint.id)} title="Start Sprint">
                      ▶️
                    </button>
                  )}
                  {sprint.status === 'ACTIVE' && (
                    <button className="btn-complete" onClick={() => handleComplete(sprint.id)} title="Complete Sprint">
                      ✅
                    </button>
                  )}
                  <button className="btn-delete" onClick={() => handleDelete(sprint.id)} title="Delete Sprint">
                    🗑️
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Create/Edit Form Modal */}
      {showForm && (
        <div className="modal-overlay" onClick={resetForm}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editingSprint ? 'Edit Sprint' : 'Create Sprint'}</h2>
              <button className="btn-close" onClick={resetForm}>×</button>
            </div>
            <form onSubmit={handleSubmit} className="sprint-form">
              {error && <div className="error-message">{error}</div>}
              <div className="form-group">
                <label>Sprint Name *</label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  className="form-input"
                />
              </div>
              <div className="form-group">
                <label>Project *</label>
                <select
                  value={formData.projectId}
                  onChange={(e) => setFormData({ ...formData, projectId: Number(e.target.value) })}
                  required
                  className="form-select"
                >
                  <option value="">Select Project</option>
                  {projects.map(project => (
                    <option key={project.id} value={project.id}>
                      {project.name}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Start Date *</label>
                  <input
                    type="date"
                    value={formData.startDate}
                    onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                    required
                    className="form-input"
                  />
                </div>
                <div className="form-group">
                  <label>End Date *</label>
                  <input
                    type="date"
                    value={formData.endDate}
                    onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                    required
                    className="form-input"
                  />
                </div>
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="form-textarea"
                  rows={3}
                />
              </div>
              <div className="form-group">
                <label>Sprint Goals</label>
                <textarea
                  value={formData.goals}
                  onChange={(e) => setFormData({ ...formData, goals: e.target.value })}
                  className="form-textarea"
                  rows={3}
                />
              </div>
              <div className="form-actions">
                <button type="button" onClick={resetForm} className="btn btn-secondary">
                  Cancel
                </button>
                <button type="submit" disabled={saving} className="btn btn-primary">
                  {saving ? 'Saving...' : (editingSprint ? 'Update' : 'Create')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Details Modal */}
      {showDetailsModal && selectedSprintForDetails && (
        <div className="modal-overlay" onClick={() => setShowDetailsModal(false)}>
          <div className="modal-content modal-large" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Sprint Details: {selectedSprintForDetails.name}</h2>
              <button className="btn-close" onClick={() => setShowDetailsModal(false)}>×</button>
            </div>
            <div className="sprint-details">
              <div className="detail-section">
                <h3>Sprint Information</h3>
                <div className="detail-grid">
                  <div><strong>Status:</strong> 
                    <span className="status-badge" style={{ backgroundColor: getStatusColor(selectedSprintForDetails.status), marginLeft: '8px' }}>
                      {selectedSprintForDetails.status}
                    </span>
                  </div>
                  <div><strong>Project:</strong> {getProjectName(selectedSprintForDetails.projectId)}</div>
                  <div><strong>Duration:</strong> {getDuration(selectedSprintForDetails)}</div>
                  <div><strong>Progress:</strong> {selectedSprintForDetails.completionPercentage || 0}%</div>
                  <div><strong>Start Date:</strong> {formatDate(selectedSprintForDetails.startDate)}</div>
                  <div><strong>End Date:</strong> {formatDate(selectedSprintForDetails.endDate)}</div>
                  {selectedSprintForDetails.actualStartDate && (
                    <div><strong>Actual Start:</strong> {formatDate(selectedSprintForDetails.actualStartDate)}</div>
                  )}
                  {selectedSprintForDetails.actualEndDate && (
                    <div><strong>Actual End:</strong> {formatDate(selectedSprintForDetails.actualEndDate)}</div>
                  )}
                </div>
                {selectedSprintForDetails.description && (
                  <div className="detail-item">
                    <strong>Description:</strong>
                    <p>{selectedSprintForDetails.description}</p>
                  </div>
                )}
                {selectedSprintForDetails.goals && (
                  <div className="detail-item">
                    <strong>Sprint Goals:</strong>
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
                        <span className="task-status" style={{ backgroundColor: getStatusColor(task.status as any) }}>
                          {task.status.replace(/_/g, ' ')}
                        </span>
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