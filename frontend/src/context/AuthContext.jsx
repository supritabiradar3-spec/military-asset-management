import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/authApi';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token') || null);
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('user');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [loading, setLoading] = useState(false);

  const login = async (username, password) => {
    setLoading(true);
    try {
      const response = await authApi.login({ username, password });
      
      const authToken = response.token;
      const userData = {
        username: response.username,
        role: response.role,
        baseId: response.baseId,
        tokenType: response.tokenType || 'Bearer',
      };

      localStorage.setItem('token', authToken);
      localStorage.setItem('user', JSON.stringify(userData));

      setToken(authToken);
      setUser(userData);
      return userData;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  };

  const isAdmin = user?.role === 'ADMIN';
  const isBaseCommander = user?.role === 'BASE_COMMANDER';
  const isLogisticsOfficer = user?.role === 'LOGISTICS_OFFICER';
  const isAuthenticated = Boolean(token && user);

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        loading,
        login,
        logout,
        isAuthenticated,
        isAdmin,
        isBaseCommander,
        isLogisticsOfficer,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
