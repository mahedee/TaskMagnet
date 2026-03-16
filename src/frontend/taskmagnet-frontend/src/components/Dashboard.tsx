import React, { useState, useEffect } from 'react';
import { projectService } from '../services/projectService';
import { taskService } from '../services/taskService';
import { categoryService } from '../services/categoryService';
import { ProjectResponse, TaskResponse, CategoryResponse } from '../types';
import './Dashboard.css';

interface DashboardStats {
  totalProjects: number;
  totalTasks: number;
  completedTasks: number;
  inProgressTasks: number;
  overdueTasks: number;
}

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [recentProjects, setRecentProjects] = useState<ProjectResponse[]>([]);
  const [recentTasks, setRecentTasks] = useState<TaskResponse[]>([]);
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [projects, tasks, categoriesData] = await Promise.all([
        projectService.getAll(),
        taskService.getAll(),
        categoryService.getAll(),
      ]);

      const now = new Date();
      const dashboardStats: DashboardStats = {
        totalProjects: projects.length,
        totalTasks: tasks.length,
        completedTasks: tasks.filter(t => t.status === 'COMPLETED').length,
        inProgressTasks: tasks.filter(t => t.status === 'IN_PROGRESS').length,
        overdueTasks: tasks.filter(
          t => t.dueDate && new Date(t.dueDate) < now && t.status !== 'COMPLETED' && t.status !== 'CANCELLED'
        ).length,
      };

      setStats(dashboardStats);
      setRecentProjects(projects.slice(0, 5));
      setRecentTasks(tasks.slice(0, 10));
      setCategories(categoriesData);
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string): string => {
    switch (status) {
      case 'COMPLETED': return '#27ae60';
      case 'IN_PROGRESS': return '#3498db';
      case 'IN_REVIEW': return '#8e44ad';
      case 'ACTIVE': return '#2980b9';
      case 'NOT_STARTED': return '#95a5a6';
      case 'PLANNING': return '#95a5a6';
      case 'CANCELLED': return '#e74c3c';
      case 'ON_HOLD': return '#f39c12';
      case 'ARCHIVED': return '#7f8c8d';
      case 'APPROVED': return '#27ae60';
      case 'REJECTED': return '#e74c3c';
      default: return '#95a5a6';
    }
  };

  const getPriorityColor = (priority: string): string => {
    switch (priority) {
      case 'CRITICAL': return '#c0392b';
      case 'URGENT': return '#e74c3c';
      case 'HIGH': return '#f39c12';
      case 'MEDIUM': return '#3498db';
      case 'LOW': return '#27ae60';
      default: return '#95a5a6';
    }
  };

  if (loading) {
    return (
      <div className="dashboard-loading">
        <div className="spinner"></div>
        <p>Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>TaskMagnet Dashboard</h1>
        <p>Welcome to your project management overview</p>
      </header>

      {/* Statistics Cards */}
      <section className="stats-section">
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-icon">📊</div>
            <div className="stat-content">
              <h3>{stats?.totalProjects || 0}</h3>
              <p>Total Projects</p>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">📋</div>
            <div className="stat-content">
              <h3>{stats?.totalTasks || 0}</h3>
              <p>Total Tasks</p>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">✅</div>
            <div className="stat-content">
              <h3>{stats?.completedTasks || 0}</h3>
              <p>Completed Tasks</p>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">🔄</div>
            <div className="stat-content">
              <h3>{stats?.inProgressTasks || 0}</h3>
              <p>In Progress</p>
            </div>
          </div>
          <div className="stat-card urgent">
            <div className="stat-icon">⚠️</div>
            <div className="stat-content">
              <h3>{stats?.overdueTasks || 0}</h3>
              <p>Overdue Tasks</p>
            </div>
          </div>
        </div>
      </section>

      <div className="dashboard-content">
        {/* Recent Projects */}
        <section className="recent-projects">
          <h2>Recent Projects</h2>
          <div className="projects-list">
            {recentProjects.map(project => (
              <div key={project.id} className="project-card">
                <div className="project-header">
                  <h3>{project.name}</h3>
                  <div className="project-badges">
                    <span 
                      className="status-badge" 
                      style={{ backgroundColor: getStatusColor(project.status) }}
                    >
                      {project.status.replace(/_/g, ' ')}
                    </span>
                  </div>
                </div>
                {project.description && <p className="project-description">{project.description}</p>}
                <div className="project-dates">
                  {project.startDate && (
                    <span>Start: {new Date(project.startDate).toLocaleDateString()}</span>
                  )}
                  {project.endDate && (
                    <span>End: {new Date(project.endDate).toLocaleDateString()}</span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* Recent Tasks */}
        <section className="recent-tasks">
          <h2>Recent Tasks</h2>
          <div className="tasks-list">
            {recentTasks.map(task => (
              <div key={task.id} className="task-item">
                <div className="task-content">
                  <h4>{task.title}</h4>
                  <p>{task.description}</p>
                  <div className="task-meta">
                    <span 
                      className="status-badge small" 
                      style={{ backgroundColor: getStatusColor(task.status) }}
                    >
                      {task.status.replace(/_/g, ' ')}
                    </span>
                    <span 
                      className="priority-badge small" 
                      style={{ backgroundColor: getPriorityColor(task.priority) }}
                    >
                      {task.priority}
                    </span>
                    {task.dueDate && (
                      <span className="due-date">
                        Due: {new Date(task.dueDate).toLocaleDateString()}
                      </span>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* Categories */}
        <section className="categories-section">
          <h2>Categories</h2>
          <div className="categories-grid">
            {categories.map(category => (
              <div key={category.id} className="category-card">
                <div 
                  className="category-color" 
                  style={{ backgroundColor: '#667eea' }}
                ></div>
                <div className="category-content">
                  <h4>{category.name}</h4>
                  {category.description && <p>{category.description}</p>}
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
};

export default Dashboard;
