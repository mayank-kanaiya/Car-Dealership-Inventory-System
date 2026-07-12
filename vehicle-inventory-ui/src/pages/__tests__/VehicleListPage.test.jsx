import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import VehicleListPage from '../VehicleListPage';

function renderPage() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={['/vehicles']}>
        <VehicleListPage />
      </MemoryRouter>
    </QueryClientProvider>
  );
}

describe('VehicleListPage', () => {
  it('shows loading spinner initially', () => {
    renderPage();
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  it('renders vehicle cards after loading', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Vehicle Inventory')).toBeInTheDocument();
    });
    expect(screen.getByText('Toyota Camry')).toBeInTheDocument();
    expect(screen.getByText('Honda Civic')).toBeInTheDocument();
    expect(screen.getByText('Ford F-150')).toBeInTheDocument();
  });

  it('renders filter controls', async () => {
    renderPage();
    await waitFor(() => {
      expect(screen.getByText('Vehicle Inventory')).toBeInTheDocument();
    });
    expect(screen.getByRole('searchbox')).toBeInTheDocument();
    expect(screen.getByLabelText('Category')).toBeInTheDocument();
  });
});
