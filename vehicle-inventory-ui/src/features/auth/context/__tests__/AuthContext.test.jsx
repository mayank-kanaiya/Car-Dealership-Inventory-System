import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, beforeEach } from 'vitest';
import '@testing-library/jest-dom/vitest';
import { AuthProvider } from '../AuthContext';
import { useAuth } from '../../hooks/useAuth';

function TestComponent() {
  const { user, isAuthenticated, isAdmin, login, logout, register, isLoading } = useAuth();
  return (
    <div>
      <span data-testid="is-authenticated">{String(isAuthenticated)}</span>
      <span data-testid="is-admin">{String(isAdmin)}</span>
      <span data-testid="is-loading">{String(isLoading)}</span>
      {user && <span data-testid="user-name">{user.fullName}</span>}
      {user && <span data-testid="user-role">{user.role}</span>}
      <button onClick={() => login({ email: 'john@example.com', password: 'password123' })}>Login</button>
      <button onClick={() => register({ fullName: 'Jane', email: 'jane@example.com', password: 'password123' })}>Register</button>
      <button onClick={logout}>Logout</button>
    </div>
  );
}

function renderWithAuth(ui) {
  return render(<AuthProvider>{ui}</AuthProvider>);
}

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('starts unauthenticated', () => {
    renderWithAuth(<TestComponent />);
    expect(screen.getByTestId('is-authenticated')).toHaveTextContent('false');
    expect(screen.getByTestId('is-admin')).toHaveTextContent('false');
  });

  it('login sets user and isAuthenticated', async () => {
    const user = userEvent.setup();
    renderWithAuth(<TestComponent />);

    await user.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(screen.getByTestId('is-authenticated')).toHaveTextContent('true');
    });
    expect(screen.getByTestId('user-name')).toHaveTextContent('John Doe');
  });

  it('logout clears user and isAuthenticated', async () => {
    const user = userEvent.setup();
    localStorage.setItem('auth_token', 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6IkpvaG4gRG9lIiwiZW1haWwiOiJqb2huQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJleHAiOjk5OTk5OTk5OTl9.signature');
    renderWithAuth(<TestComponent />);

    await waitFor(() => {
      expect(screen.getByTestId('is-authenticated')).toHaveTextContent('true');
    });

    await user.click(screen.getByRole('button', { name: /logout/i }));

    expect(screen.getByTestId('is-authenticated')).toHaveTextContent('false');
    expect(localStorage.getItem('auth_token')).toBeNull();
  });

  it('persists token in localStorage after login', async () => {
    const user = userEvent.setup();
    renderWithAuth(<TestComponent />);

    await user.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(localStorage.getItem('auth_token')).toBeDefined();
    });
  });

  it('restores session on mount from localStorage', async () => {
    const payload = { sub: '1', name: 'John Doe', email: 'john@example.com', role: 'USER', exp: 9999999999 };
    const encoded = btoa(JSON.stringify(payload));
    localStorage.setItem('auth_token', `header.${encoded}.sig`);

    renderWithAuth(<TestComponent />);

    await waitFor(() => {
      expect(screen.getByTestId('is-authenticated')).toHaveTextContent('true');
    });
    expect(screen.getByTestId('user-name')).toHaveTextContent('John Doe');
  });

  it('isAdmin is true for ADMIN role', async () => {
    const payload = { sub: '1', name: 'Admin User', email: 'admin@example.com', role: 'ADMIN', exp: 9999999999 };
    const encoded = btoa(JSON.stringify(payload));
    localStorage.setItem('auth_token', `header.${encoded}.sig`);

    renderWithAuth(<TestComponent />);

    await waitFor(() => {
      expect(screen.getByTestId('is-admin')).toHaveTextContent('true');
    });
  });
});
