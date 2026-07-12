import { createContext, useState, useCallback, useEffect } from 'react';
import PropTypes from 'prop-types';
import authService from '../services/authService';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  const restoreSession = useCallback(() => {
    const token = authService.getToken();
    if (token && !authService.isTokenExpired(token)) {
      const userData = authService.getUserFromToken(token);
      setUser(userData);
    } else {
      authService.removeToken();
      setUser(null);
    }
    setIsLoading(false);
  }, []);

  useEffect(() => {
    restoreSession();
  }, [restoreSession]);

  const login = useCallback(async ({ email, password }) => {
    const result = await authService.login({ email, password });
    authService.setToken(result.token);
    const userData = authService.getUserFromToken(result.token);
    setUser(userData);
    return result;
  }, []);

  const register = useCallback(async ({ fullName, email, password }) => {
    const result = await authService.register({ fullName, email, password });
    authService.setToken(result.token);
    const userData = authService.getUserFromToken(result.token);
    setUser(userData);
    return result;
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
  }, []);

  const value = {
    user,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ADMIN',
    isLoading,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired,
};
