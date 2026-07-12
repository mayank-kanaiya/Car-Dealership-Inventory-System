import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import GuestRoute from '../GuestRoute';

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
        <Route element={<GuestRoute />}>
          <Route path="/" element={<div>Guest Content</div>} />
        </Route>
        <Route path="/vehicles" element={<div>Vehicles Page</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe('GuestRoute', () => {
  it('renders children when not authenticated', () => {
    renderWithRouter({ isAuthenticated: false });
    expect(screen.getByText(/guest content/i)).toBeInTheDocument();
  });

  it('redirects to /vehicles when already authenticated', () => {
    renderWithRouter({ isAuthenticated: true });
    expect(screen.getByText(/vehicles page/i)).toBeInTheDocument();
    expect(screen.queryByText(/guest content/i)).not.toBeInTheDocument();
  });
});
