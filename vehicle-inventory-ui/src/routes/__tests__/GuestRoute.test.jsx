import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import GuestRoute from '../GuestRoute';

vi.mock('../../features/auth/hooks/useAuth', () => ({
  useAuth: vi.fn(),
}));

import { useAuth } from '../../features/auth/hooks/useAuth';

function renderWithRouter(ui, initialRoute = '/') {
  return render(
    <MemoryRouter initialEntries={[initialRoute]}>
      <Routes>
        <Route path="/" element={ui} />
        <Route path="/dashboard" element={<div>Dashboard Page</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe('GuestRoute', () => {
  it('renders children when not authenticated', () => {
    vi.mocked(useAuth).mockReturnValue({ isAuthenticated: false, isAdmin: false, user: null });
    renderWithRouter(<GuestRoute><div>Guest Content</div></GuestRoute>);
    expect(screen.getByText(/guest content/i)).toBeInTheDocument();
  });

  it('redirects to /dashboard when already authenticated', () => {
    vi.mocked(useAuth).mockReturnValue({ isAuthenticated: true, isAdmin: false, user: { role: 'USER' } });
    renderWithRouter(<GuestRoute><div>Guest Content</div></GuestRoute>);
    expect(screen.getByText(/dashboard page/i)).toBeInTheDocument();
    expect(screen.queryByText(/guest content/i)).not.toBeInTheDocument();
  });
});
