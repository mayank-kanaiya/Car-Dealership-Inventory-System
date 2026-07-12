import { describe, it, expect, beforeEach } from 'vitest';
import authService from '../authService';

describe('authService', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  describe('login', () => {
    it('sends login request and returns token', async () => {
      const result = await authService.login({
        email: 'john@example.com',
        password: 'password123',
      });
      expect(result.token).toBeDefined();
      expect(result.message).toBe('Login successful');
    });

    it('throws error for invalid credentials', async () => {
      await expect(
        authService.login({ email: 'john@example.com', password: 'wrongpassword' })
      ).rejects.toThrow();
    });
  });

  describe('register', () => {
    it('sends register request and returns token', async () => {
      const result = await authService.register({
        fullName: 'John Doe',
        email: 'john@example.com',
        password: 'password123',
      });
      expect(result.token).toBeDefined();
    });

    it('throws error for duplicate email', async () => {
      await expect(
        authService.register({
          fullName: 'John',
          email: 'existing@example.com',
          password: 'pass1234',
        })
      ).rejects.toThrow();
    });
  });

  describe('token management', () => {
    it('saves token to localStorage', () => {
      authService.setToken('test-token');
      expect(localStorage.getItem('auth_token')).toBe('test-token');
    });

    it('retrieves token from localStorage', () => {
      localStorage.setItem('auth_token', 'saved-token');
      expect(authService.getToken()).toBe('saved-token');
    });

    it('removes token from localStorage', () => {
      localStorage.setItem('auth_token', 'to-remove');
      authService.removeToken();
      expect(localStorage.getItem('auth_token')).toBeNull();
    });
  });

  describe('decodeToken', () => {
    it('decodes a valid JWT payload', () => {
      const payload = {
        sub: '1',
        name: 'John Doe',
        email: 'john@example.com',
        role: 'USER',
        exp: 9999999999,
      };
      const encoded = btoa(JSON.stringify(payload));
      const token = `header.${encoded}.sig`;

      const decoded = authService.decodeToken(token);
      expect(decoded.name).toBe('John Doe');
      expect(decoded.role).toBe('USER');
    });

    it('returns null for invalid token', () => {
      expect(authService.decodeToken('invalid')).toBeNull();
    });
  });

  describe('isTokenExpired', () => {
    it('returns true for expired token', () => {
      const payload = { exp: 0 };
      const encoded = btoa(JSON.stringify(payload));
      const token = `header.${encoded}.sig`;
      expect(authService.isTokenExpired(token)).toBe(true);
    });

    it('returns false for valid token', () => {
      const payload = { exp: 9999999999 };
      const encoded = btoa(JSON.stringify(payload));
      const token = `header.${encoded}.sig`;
      expect(authService.isTokenExpired(token)).toBe(false);
    });
  });

  describe('getUserFromToken', () => {
    it('returns user object from valid token', () => {
      const payload = {
        sub: '1',
        name: 'John Doe',
        email: 'john@example.com',
        role: 'USER',
        exp: 9999999999,
      };
      const encoded = btoa(JSON.stringify(payload));
      const token = `header.${encoded}.sig`;

      const user = authService.getUserFromToken(token);
      expect(user).toEqual({
        id: '1',
        fullName: 'John Doe',
        email: 'john@example.com',
        role: 'USER',
      });
    });

    it('returns null for no token', () => {
      expect(authService.getUserFromToken(null)).toBeNull();
    });
  });

  describe('logout', () => {
    it('removes token from localStorage', () => {
      localStorage.setItem('auth_token', 'to-clear');
      authService.logout();
      expect(localStorage.getItem('auth_token')).toBeNull();
    });
  });
});
