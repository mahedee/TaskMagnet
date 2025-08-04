import React, { useState, useEffect } from 'react';
import { jsonRepository } from '../services/jsonRepository';
import { Project, Category, CreateProjectRequest, UpdateProjectRequest } from '../types';
import './ProjectManager.css';

const ProjectManager: React.FC = () => {
  const [projects, setProjects] = useState<Project[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingProject, setEditingProject] = useState<Project | null>(null);

  // Form state
  const [formData, setFormData] = useState<CreateProjectRequest>({
    name: '',
    description: '',
    startDate: '',
    endDate: '',
    status: 'PLANNING',
    priority: 'MEDIUM',
    categoryId: ''
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [projectsData, categoriesData] = await Promise.all([
        jsonRepository.getProjects(),
        jsonRepository.getCategories()
      ]);
      setProjects(projectsData);
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
      if (editingProject) {
        const updateRequest: UpdateProjectRequest = {
          id: editingProject.id,
          ...formData
        };
        await jsonRepository.updateProject(updateRequest);
      } else {
        await jsonRepository.createProject(formData, '1'); // Mock user ID
      }
      
      await loadData();
      resetForm();
    } catch (error) {
      console.error('Error saving project:', error);
    }
  };

  const handleEdit = (project: Project) => {
    setEditingProject(project);
    setFormData({
      name: project.name,
      description: project.description || '',
      startDate: project.startDate || '',
      endDate: project.endDate || '',
      status: project.status,
      priority: project.priority,
      categoryId: project.categoryId || ''
    });
    setShowForm(true);
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this project?')) {
      try {
        await jsonRepository.deleteProject(id);
        await loadData();
      } catch (error) {
        console.error('Error deleting project:', error);
      }
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      description: '',
      startDate: '',
      endDate: '',
      status: 'PLANNING',
      priority: 'MEDIUM',
      categoryId: ''
    });
    setEditingProject(null);
    setShowForm(false);
  };

  const getStatusColor = (status: string): string => {
    switch (status) {
      case 'COMPLETED': return '#27ae60';
      case 'IN_PROGRESS': return '#3498db';
      case 'PLANNING': return '#95a5a6';
      case 'ON_HOLD': return '#f39c12';
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

  const getCategoryName = (categoryId?: string): string => {
    if (!categoryId) return 'No Category';
    const category = categories.find(c => c.id === categoryId);
    return category?.name || 'Unknown Category';
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

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Status</label>
                  <select
                    className="form-control"
                    value={formData.status}
                    onChange={(e) => setFormData({...formData, status: e.target.value as any})}
                  >
                    <option value="PLANNING">Planning</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="COMPLETED">Completed</option>
                    <option value="ON_HOLD">On Hold</option>
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

              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={resetForm}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  {editingProject ? 'Update Project' : 'Create Project'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="projects-grid">
        {projects.length === 0 ? (
          <div className="no-projects">
            <h3>No projects found</h3>
            <p>Create your first project to get started!</p>
          </div>
        ) : (
          projects.map(project => (
            <div key={project.id} className="project-card">
              <div className="project-card-header">
                <h3>{project.name}</h3>
                <div className="project-actions">
                  <button 
                    className="btn-icon"
                    onClick={() => handleEdit(project)}
                    title="Edit"
                  >
                    ✏️
                  </button>
                  <button 
                    className="btn-icon"
                    onClick={() => handleDelete(project.id)}
                    title="Delete"
                  >
                    🗑️
                  </button>
                </div>
              </div>
              
              <div className="project-card-body">
                <p className="project-description">{project.description}</p>
                
                <div className="project-meta">
                  <div className="meta-item">
                    <strong>Category:</strong> {getCategoryName(project.categoryId)}
                  </div>
                  {project.startDate && (
                    <div className="meta-item">
                      <strong>Start:</strong> {new Date(project.startDate).toLocaleDateString()}
                    </div>
                  )}
                  {project.endDate && (
                    <div className="meta-item">
                      <strong>End:</strong> {new Date(project.endDate).toLocaleDateString()}
                    </div>
                  )}
                </div>
                
                <div className="project-badges">
                  <span 
                    className="status-badge" 
                    style={{ backgroundColor: getStatusColor(project.status) }}
                  >
                    {project.status.replace('_', ' ')}
                  </span>
                  <span 
                    className="priority-badge" 
                    style={{ backgroundColor: getPriorityColor(project.priority) }}
                  >
                    {project.priority}
                  </span>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default ProjectManager;
