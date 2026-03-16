import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import './Navigation.css';

interface NavigationProps {
  currentView: string;
  onViewChange: (view: string) => void;
}

const Navigation: React.FC<NavigationProps> = ({ currentView, onViewChange }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const navigationItems = [
    { id: 'dashboard', label: 'Dashboard', icon: '📊' },
    { id: 'projects', label: 'Projects', icon: '📁' },
    { id: 'tasks', label: 'Tasks', icon: '✅' },
    { id: 'categories', label: 'Categories', icon: '🏷️' }
  ];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const primaryRole = user?.roles?.[0]?.replace('ROLE_', '') ?? 'User';

  return (
    <nav className="navigation">
      <div className="nav-brand">
        <h2>🧲 TaskMagnet</h2>
      </div>

      <ul className="nav-menu">
        {navigationItems.map(item => (
          <li key={item.id} className="nav-item">
            <button
              className={`nav-link ${currentView === item.id ? 'active' : ''}`}
              onClick={() => onViewChange(item.id)}
            >
              <span className="nav-icon">{item.icon}</span>
              <span className="nav-label">{item.label}</span>
            </button>
          </li>
        ))}
      </ul>

      <div className="nav-footer">
        <div className="user-info">
          <div className="user-avatar">👤</div>
          <div className="user-details">
            <div className="user-name">{user?.fullName ?? user?.username ?? 'User'}</div>
            <div className="user-role">{primaryRole}</div>
          </div>
        </div>
        <button className="logout-btn" onClick={handleLogout} title="Sign out">🚪</button>
      </div>
    </nav>
  );
};

export default Navigation;
