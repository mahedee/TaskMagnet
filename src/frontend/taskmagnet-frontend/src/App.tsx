import React, { useState } from 'react';
import Navigation from './components/Navigation';
import Dashboard from './components/Dashboard';
import ProjectManager from './components/ProjectManager';
import TaskManager from './components/TaskManager';
import './App.css';

function App() {
  const [currentView, setCurrentView] = useState('dashboard');

  const renderCurrentView = () => {
    switch (currentView) {
      case 'dashboard':
        return <Dashboard />;
      case 'projects':
        return <ProjectManager />;
      case 'tasks':
        return <TaskManager />;
      case 'categories':
        return (
          <div className="coming-soon">
            <h2>Category Manager</h2>
            <p>Category management interface coming soon!</p>
          </div>
        );
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="App">
      <Navigation currentView={currentView} onViewChange={setCurrentView} />
      <main className="main-content">
        {renderCurrentView()}
      </main>
    </div>
  );
}

export default App;
