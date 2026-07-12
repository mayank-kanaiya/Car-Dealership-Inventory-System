import apiClient from '../../../services/apiClient';

const TOKEN_KEY = 'auth_token';

const authService = {
  async login({ email, password }) {
    const response = await apiClient.post('/auth/login', { email, password });
    return response.data;
  },

  async register({ fullName, email, password }) {
    const response = await apiClient.post('/auth/register', { fullName, email, password });
    return response.data;
  },

  setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
  },

  getToken() {
    return localStorage.getItem(TOKEN_KEY);
  },

  removeToken() {
    localStorage.removeItem(TOKEN_KEY);
  },

  logout() {
    this.removeToken();
  },

  decodeToken(token) {
    try {
      if (!token) return null;
      const parts = token.split('.');
      if (parts.length !== 3) return null;
      const payload = JSON.parse(atob(parts[1]));
      return payload;
    } catch {
      return null;
    }
  },

  isTokenExpired(token) {
    const payload = this.decodeToken(token);
    if (!payload || !payload.exp) return true;
    return payload.exp * 1000 < Date.now();
  },

  getUserFromToken(token) {
    const payload = this.decodeToken(token);
    if (!payload) return null;
    return {
      id: payload.sub,
      fullName: payload.name,
      email: payload.email,
      role: payload.role,
    };
  },
};

export default authService;
