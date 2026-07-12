import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import ProtectedRoute from '../ProtectedRoute';

vi.mock('../../features/auth/hooks/useAuth', () => ({
  useAuth: vi.fn(),
}));

import { useAuth } from '../../features/auth/hooks/useAuth';

function renderWithRouter({ isAuthenticated = false, isLoading = false, route = '/' }) {
  vi.mocked(useAuth).mockReturnValue({
    isAuthenticated,
    isAdmin: isAuthenticated,
    isLoading,
    user: isAuthenticated ? { role: 'USER' } : null,
  });
  return render(
    <MemoryRouter initialEntries={[route]}>
      <Routes>
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<div>Protected Content</div>} />
        </Route>
        <Route path="/login" element={<div>Login Page</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe('ProtectedRoute', () => {
  it('renders children when authenticated', () => {
    renderWithRouter({ isAuthenticated: true });
    expect(screen.getByText(/protected content/i)).toBeInTheDocument();
  });

  it('redirects to /login when not authenticated', () => {
    renderWithRouter({ isAuthenticated: false });
    expect(screen.getByText(/login page/i)).toBeInTheDocument();
    expect(screen.queryByText(/protected content/i)).not.toBeInTheDocument();
  });

  it('shows loading state while checking auth', () => {
    renderWithRouter({ isLoading: true });
    expect(screen.getByRole('status')).toBeInTheDocument();
  });
});
