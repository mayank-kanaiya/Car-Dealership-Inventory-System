import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import AdminRoute from '../AdminRoute';

vi.mock('../../features/auth/hooks/useAuth', () => ({
  useAuth: vi.fn(),
}));

import { useAuth } from '../../features/auth/hooks/useAuth';

function renderWithRouter({
  isAuthenticated = false,
  isAdmin = false,
  isLoading = false,
  route = '/',
}) {
  vi.mocked(useAuth).mockReturnValue({
    isAuthenticated,
    isAdmin,
    isLoading,
    user: isAuthenticated ? { role: isAdmin ? 'ADMIN' : 'USER' } : null,
  });
  return render(
    <MemoryRouter initialEntries={[route]}>
      <Routes>
        <Route element={<AdminRoute />}>
          <Route path="/" element={<div>Admin Content</div>} />
        </Route>
        <Route path="/login" element={<div>Login Page</div>} />
        <Route path="/vehicles" element={<div>Vehicles Page</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe('AdminRoute', () => {
  it('renders children when user is ADMIN', () => {
    renderWithRouter({ isAuthenticated: true, isAdmin: true });
    expect(screen.getByText(/admin content/i)).toBeInTheDocument();
  });

  it('redirects to /vehicles when user is not ADMIN', () => {
    renderWithRouter({ isAuthenticated: true, isAdmin: false });
    expect(screen.getByText(/vehicles page/i)).toBeInTheDocument();
    expect(screen.queryByText(/admin content/i)).not.toBeInTheDocument();
  });

  it('redirects to /login when not authenticated', () => {
    renderWithRouter({ isAuthenticated: false });
    expect(screen.getByText(/login page/i)).toBeInTheDocument();
  });
});
