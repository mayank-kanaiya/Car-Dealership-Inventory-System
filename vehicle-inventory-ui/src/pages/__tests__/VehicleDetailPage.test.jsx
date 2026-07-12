import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import VehicleDetailPage from '../VehicleDetailPage';

function renderDetailPage(vehicleId = '550e8400-e29b-41d4-a716-446655440000') {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[`/vehicles/${vehicleId}`]}>
        <Routes>
          <Route path="/vehicles/:id" element={<VehicleDetailPage />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>
  );
}

describe('VehicleDetailPage', () => {
  it('shows loading spinner initially', () => {
    renderDetailPage();
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  it('renders vehicle details after loading', async () => {
    renderDetailPage();
    await waitFor(() => {
      expect(screen.getByText('Toyota Camry')).toBeInTheDocument();
    });
    expect(screen.getByText('$28,500.00')).toBeInTheDocument();
    expect(screen.getByText('Sedan')).toBeInTheDocument();
    expect(screen.getByText('15 units in stock')).toBeInTheDocument();
  });

  it('renders back to list link', async () => {
    renderDetailPage();
    await waitFor(() => {
      expect(screen.getByText('Toyota Camry')).toBeInTheDocument();
    });
    expect(screen.getByText(/back to inventory/i)).toBeInTheDocument();
  });
});
