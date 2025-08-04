import React from 'react';
import './Navigation.css';

interface NavigationProps {
  currentView: string;
  onViewChange: (view: string) => void;
}

const Navigation: React.FC<NavigationProps> = ({ currentView, onViewChange }) => {
  const navigationItems = [
    { id: 'dashboard', label: 'Dashboard', icon: '📊' },
    { id: 'projects', label: 'Projects', icon: '📁' },
    { id: 'tasks', label: 'Tasks', icon: '✅' },
    { id: 'categories', label: 'Categories', icon: '🏷️' }
  ];

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
            <div className="user-name">Admin User</div>
            <div className="user-role">Administrator</div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navigation;
