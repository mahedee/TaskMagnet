import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import Navigation from './components/Navigation';
import Dashboard from './components/Dashboard';
import ProjectManager from './components/ProjectManager';
import TaskManager from './components/TaskManager';
import SprintManager from './components/SprintManager';
import TaskStatusManager from './components/TaskStatusManager';
import CategoryManager from './components/CategoryManager';
import './App.css';

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, user } = useAuth();
  console.log('ProtectedRoute check:', { isAuthenticated, user: user?.username, hasToken: !!user?.token });
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
};

const AppLayout: React.FC = () => {
  const [currentView, setCurrentView] = useState('dashboard');

  const renderCurrentView = () => {
    switch (currentView) {
      case 'dashboard':
        return <Dashboard />;
      case 'projects':
        return <ProjectManager />;
      case 'tasks':
        return <TaskManager />;
      case 'sprints':
        return <SprintManager />;
      case 'task-statuses':
        return <TaskStatusManager />;
      case 'categories':
        return <CategoryManager />;
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
};

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route
            path="/*"
            element={
              <ProtectedRoute>
                <AppLayout />
              </ProtectedRoute>
            }
          />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
