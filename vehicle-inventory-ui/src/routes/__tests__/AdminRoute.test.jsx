import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import AdminRoute from '../AdminRoute';

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
        <Route path="/unauthorized" element={<div>Unauthorized Page</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe('AdminRoute', () => {
  it('renders children when user is ADMIN', () => {
    vi.mocked(useAuth).mockReturnValue({ isAuthenticated: true, isAdmin: true, user: { role: 'ADMIN' } });
    renderWithRouter(<AdminRoute><div>Admin Content</div></AdminRoute>);
    expect(screen.getByText(/admin content/i)).toBeInTheDocument();
  });

  it('redirects to /unauthorized when user is not ADMIN', () => {
    vi.mocked(useAuth).mockReturnValue({ isAuthenticated: true, isAdmin: false, user: { role: 'USER' } });
    renderWithRouter(<AdminRoute><div>Admin Content</div></AdminRoute>);
    expect(screen.getByText(/unauthorized page/i)).toBeInTheDocument();
    expect(screen.queryByText(/admin content/i)).not.toBeInTheDocument();
  });

  it('redirects to /login when not authenticated', () => {
    vi.mocked(useAuth).mockReturnValue({ isAuthenticated: false, isAdmin: false, user: null });
    renderWithRouter(<AdminRoute><div>Admin Content</div></AdminRoute>);
    expect(screen.getByText(/login page/i)).toBeInTheDocument();
  });
});
