import React, { createContext, useContext, useState, useEffect } from 'react';
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
    if (!stored) {
      console.log('No stored auth found');
      return null;
    }
    try {
      const auth = JSON.parse(stored) as AuthResponse;
      console.log('Loaded stored auth:', { username: auth.username, hasToken: !!auth.token });
      // For now, don't validate expiration during initialization
      // Let the API interceptor handle expired tokens
      return auth;
    } catch (error) {
      console.log('Error parsing stored auth, clearing:', error);
      localStorage.removeItem('auth');
      localStorage.removeItem('loginTime');
      return null;
    }
  });

  // Track user state changes for debugging
  useEffect(() => {
    console.log('User state changed:', { 
      user: user?.username, 
      isAuthenticated: !!user, 
      hasToken: !!user?.token,
      timestamp: new Date().toISOString()
    });
  }, [user]);

  // Track user state changes
  useEffect(() => {
    console.log('User state changed:', { 
      user: user?.username, 
      isAuthenticated: !!user, 
      hasToken: !!user?.token,
      timestamp: new Date().toISOString()
    });
  }, [user]);

  const login = async (req: LoginRequest): Promise<void> => {
    try {
      console.log('Attempting login with:', req.usernameOrEmail);
      const response = await authService.login(req);
      console.log('Login successful:', response);
      setUser(response);
      localStorage.setItem('auth', JSON.stringify(response));
      localStorage.setItem('loginTime', Date.now().toString());
    } catch (error) {
      console.error('Login failed:', error);
      // Clear any corrupted auth data
      localStorage.removeItem('auth');
      localStorage.removeItem('loginTime');
      throw error;
    }
  };

  const signup = async (req: SignupRequest): Promise<void> => {
    const response = await authService.signup(req);
    setUser(response);
    localStorage.setItem('auth', JSON.stringify(response));
    localStorage.setItem('loginTime', Date.now().toString());
  };

  const logout = (): void => {
    setUser(null);
    localStorage.removeItem('auth');
    localStorage.removeItem('loginTime');
  };

  const isAuthenticated = !!user;
  console.log('AuthContext render:', { user: user?.username, isAuthenticated, hasToken: !!user?.token });
  
  return (
    <AuthContext.Provider value={{ user, login, signup, logout, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};
