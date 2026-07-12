import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import ProtectedRoute from '../ProtectedRoute';

vi.mock('../../features/auth/hooks/useAuth', () => ({
  useAuth: vi.fn(),
}));

import { useAuth } from '../../features/auth/hooks/useAuth';

function renderWithRouter(ui, initialRoute = '/') {
  return render(
    <MemoryRouter initialEntries={[initialRoute]}>
      <Routes>
        <Route path="/" element={ui} />
        <Route path="/login" element={<div>Login Page</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe('ProtectedRoute', () => {
  it('renders children when authenticated', () => {
    vi.mocked(useAuth).mockReturnValue({ isAuthenticated: true, isAdmin: false, user: { role: 'USER' } });
    renderWithRouter(<ProtectedRoute><div>Protected Content</div></ProtectedRoute>);
    expect(screen.getByText(/protected content/i)).toBeInTheDocument();
  });

  it('redirects to /login when not authenticated', () => {
    vi.mocked(useAuth).mockReturnValue({ isAuthenticated: false, isAdmin: false, user: null });
    renderWithRouter(<ProtectedRoute><div>Protected Content</div></ProtectedRoute>);
    expect(screen.getByText(/login page/i)).toBeInTheDocument();
    expect(screen.queryByText(/protected content/i)).not.toBeInTheDocument();
  });

  it('shows loading state while checking auth', () => {
    vi.mocked(useAuth).mockReturnValue({ isAuthenticated: false, isAdmin: false, user: null, isLoading: true });
    renderWithRouter(<ProtectedRoute><div>Protected</div></ProtectedRoute>);
    expect(screen.getByRole('status')).toBeInTheDocument();
  });
});
