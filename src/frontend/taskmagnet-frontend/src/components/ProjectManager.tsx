import React, { useState, useEffect } from 'react';
import { projectService } from '../services/projectService';
import { categoryService } from '../services/categoryService';
import { useAuth } from '../contexts/AuthContext';
import { ProjectResponse, CategoryResponse, ProjectRequest, ProjectStatus } from '../types';
import './ProjectManager.css';

const STATUS_COLORS: Record<string, string> = {
  PLANNING: '#95a5a6',
  ACTIVE: '#3498db',
  COMPLETED: '#27ae60',
  ON_HOLD: '#f39c12',
  CANCELLED: '#e74c3c',
  ARCHIVED: '#7f8c8d',
};

const EMPTY_FORM = (ownerId: number): ProjectRequest => ({
  name: '',
  code: '',
  description: '',
  startDate: '',
  endDate: '',
  status: 'PLANNING',
  ownerId,
});

const ProjectManager: React.FC = () => {
  const { user } = useAuth();
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingProject, setEditingProject] = useState<ProjectResponse | null>(null);
  const [formData, setFormData] = useState<ProjectRequest>(EMPTY_FORM(0));
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [projectsData, categoriesData] = await Promise.all([
        projectService.getAll(),
        categoryService.getAll(),
      ]);
      setProjects(projectsData);
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
      const payload: ProjectRequest = { ...formData, ownerId: user!.id };
      if (editingProject) {
        await projectService.update(editingProject.id, payload);
      } else {
        await projectService.create(payload);
      }
      await loadData();
      resetForm();
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ||
        'Failed to save project.';
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (project: ProjectResponse) => {
    setEditingProject(project);
    setFormData({
      name: project.name,
      code: project.code,
      description: project.description ?? '',
      startDate: project.startDate ?? '',
      endDate: project.endDate ?? '',
      status: project.status,
      ownerId: project.ownerId,
      categoryId: project.categoryId,
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this project?')) return;
    try {
      await projectService.delete(id);
      await loadData();
    } catch (err) {
      console.error('Error deleting project:', err);
    }
  };

  const resetForm = () => {
    setFormData(EMPTY_FORM(user?.id ?? 0));
    setEditingProject(null);
    setShowForm(false);
    setError('');
  };

  const getStatusColor = (status: string): string => {
    return STATUS_COLORS[status] ?? '#95a5a6';
  };

  const getCategoryName = (categoryId?: number): string => {
    if (!categoryId) return 'No Category';
    return categories.find(c => c.id === categoryId)?.name ?? 'Unknown Category';
  };

  if (loading) {
    return (
      <div className="project-manager-loading">
        <div className="spinner"></div>
        <p>Loading projects...</p>
      </div>
    );
  }

  return (
    <div className="project-manager">
      <header className="project-manager-header">
        <h1>Project Management</h1>
        <button 
          className="btn btn-primary"
          onClick={() => setShowForm(true)}
        >
          + New Project
        </button>
      </header>

      {showForm && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>{editingProject ? 'Edit Project' : 'Create New Project'}</h2>
              <button 
                className="close-btn"
                onClick={resetForm}
              >
                ×
              </button>
            </div>
            
            <form onSubmit={handleSubmit} className="project-form">
              {error && <div className="form-error">{error}</div>}
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Project Name *</label>
                  <input
                    type="text"
                    className="form-control"
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Project Code * (2–20 chars)</label>
                  <input
                    type="text"
                    className="form-control"
                    value={formData.code}
                    onChange={(e) => setFormData({...formData, code: e.target.value.toUpperCase()})}
                    required
                    minLength={2}
                    maxLength={20}
                    placeholder="e.g. PROJ-01"
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Category</label>
                  <select
                    className="form-control"
                    value={formData.categoryId ?? ''}
                    onChange={(e) => setFormData({...formData, categoryId: e.target.value ? Number(e.target.value) : undefined})}
                  >
                    <option value="">No Category</option>
                    {categories.map(category => (
                      <option key={category.id} value={category.id}>
                        {category.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Status</label>
                  <select
                    className="form-control"
                    value={formData.status}
                    onChange={(e) => setFormData({...formData, status: e.target.value as ProjectStatus})}
                  >
                    <option value="PLANNING">Planning</option>
                    <option value="ACTIVE">Active</option>
                    <option value="ON_HOLD">On Hold</option>
                    <option value="COMPLETED">Completed</option>
                    <option value="CANCELLED">Cancelled</option>
                    <option value="ARCHIVED">Archived</option>
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
                  <label className="form-label">Start Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.startDate}
                    onChange={(e) => setFormData({...formData, startDate: e.target.value})}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">End Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.endDate}
                    onChange={(e) => setFormData({...formData, endDate: e.target.value})}
                  />
                </div>
              </div>

              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={resetForm}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Saving...' : editingProject ? 'Update Project' : 'Create Project'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="projects-list">
        {projects.length === 0 ? (
          <div className="no-projects">
            <span className="empty-icon">💼</span>
            <h3>No projects found</h3>
            <p>Create your first project to get started!</p>
            <button className="btn btn-primary" onClick={() => setShowForm(true)}>
              + New Project
            </button>
          </div>
        ) : (
          <div className="project-list">
            <div className="project-list-header">
              <div className="project-list-col project-list-col--icon"></div>
              <div className="project-list-col project-list-col--name">Project</div>
              <div className="project-list-col project-list-col--category">Category</div>
              <div className="project-list-col project-list-col--owner">Owner</div>
              <div className="project-list-col project-list-col--status">Status</div>
              <div className="project-list-col project-list-col--dates">Timeline</div>
              <div className="project-list-col project-list-col--actions">Actions</div>
            </div>
            {projects.map(project => (
              <div key={project.id} className="project-list-item">
                <div className="project-list-col project-list-col--icon">
                  <span className="project-icon">💼</span>
                </div>
                <div className="project-list-col project-list-col--name">
                  <div className="project-name-info">
                    <h3 className="project-name">{project.name}</h3>
                    <span className="project-code">[{project.code}]</span>
                    {project.description && (
                      <p className="project-description">{project.description}</p>
                    )}
                  </div>
                </div>
                <div className="project-list-col project-list-col--category">
                  <span className="project-category">{getCategoryName(project.categoryId)}</span>
                </div>
                <div className="project-list-col project-list-col--owner">
                  <span className="project-owner">{project.ownerUsername}</span>
                </div>
                <div className="project-list-col project-list-col--status">
                  <span 
                    className="status-badge" 
                    style={{ backgroundColor: getStatusColor(project.status) }}
                  >
                    {project.status.replace(/_/g, ' ')}
                  </span>
                </div>
                <div className="project-list-col project-list-col--dates">
                  <div className="project-timeline">
                    {project.startDate && (
                      <div className="timeline-item">
                        <small>Start: {new Date(project.startDate).toLocaleDateString()}</small>
                      </div>
                    )}
                    {project.endDate && (
                      <div className="timeline-item">
                        <small>End: {new Date(project.endDate).toLocaleDateString()}</small>
                      </div>
                    )}
                    {!project.startDate && !project.endDate && (
                      <span className="text-muted">No timeline</span>
                    )}
                  </div>
                </div>
                <div className="project-list-col project-list-col--actions">
                  <button 
                    className="btn-icon"
                    onClick={() => handleEdit(project)}
                    title="Edit"
                  >
                    ✏️
                  </button>
                  <button 
                    className="btn-icon btn-icon--danger"
                    onClick={() => handleDelete(project.id)}
                    title="Delete"
                  >
                    🗑️
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default ProjectManager;
