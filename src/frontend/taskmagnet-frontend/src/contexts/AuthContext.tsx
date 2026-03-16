import React, { createContext, useContext, useState } from 'react';
import { AuthResponse, LoginRequest, SignupRequest } from '../types';
import { authService } from '../services/authService';

interface AuthContextType {
  user: AuthResponse | null;
  login: (req: LoginRequest) => Promise<void>;
  signup: (req: SignupRequest) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthResponse | null>(() => {
    const stored = localStorage.getItem('auth');
    if (!stored) return null;
    try {
      return JSON.parse(stored) as AuthResponse;
    } catch {
      return null;
    }
  });

  const login = async (req: LoginRequest): Promise<void> => {
    const response = await authService.login(req);
    setUser(response);
    localStorage.setItem('auth', JSON.stringify(response));
  };

  const signup = async (req: SignupRequest): Promise<void> => {
    const response = await authService.signup(req);
    setUser(response);
    localStorage.setItem('auth', JSON.stringify(response));
  };

  const logout = (): void => {
    setUser(null);
    localStorage.removeItem('auth');
  };

  return (
    <AuthContext.Provider value={{ user, login, signup, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};
