import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import AdminDashboardPage from '../AdminDashboardPage';

function renderPage() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={['/admin']}>
        <AdminDashboardPage />
      </MemoryRouter>
    </QueryClientProvider>
  );
}

describe('AdminDashboardPage', () => {
  it('shows loading initially', () => {
    renderPage();
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  it('renders dashboard after loading', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Admin Dashboard')).toBeInTheDocument();
    });
    expect(screen.getByText('Add Vehicle')).toBeInTheDocument();
  });

  it('renders vehicle cards with action buttons', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Admin Dashboard')).toBeInTheDocument();
    });
    expect(screen.getAllByText(/edit/i).length).toBeGreaterThan(0);
    expect(screen.getAllByText(/sell/i).length).toBeGreaterThan(0);
    expect(screen.getAllByText(/restock/i).length).toBeGreaterThan(0);
  });
});
