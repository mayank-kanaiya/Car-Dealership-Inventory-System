import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import { CreateVehiclePage, EditVehiclePage } from '../VehicleFormPages';

function renderWithProviders(ui, entry = '/') {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[entry]}>
        <Routes>
          <Route path="/vehicles/new" element={<CreateVehiclePage />} />
          <Route path="/vehicles/:id/edit" element={<EditVehiclePage />} />
          <Route path="/vehicles" element={<div>Vehicles List</div>} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>
  );
}

describe('CreateVehiclePage', () => {
  it('renders create form', () => {
    renderWithProviders(<CreateVehiclePage />, '/vehicles/new');
    expect(screen.getByText('Add New Vehicle')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /create vehicle/i })).toBeInTheDocument();
  });

  it('renders cancel button that navigates back', () => {
    renderWithProviders(<CreateVehiclePage />, '/vehicles/new');
    expect(screen.getByRole('button', { name: /cancel/i })).toBeInTheDocument();
  });
});

describe('EditVehiclePage', () => {
  it('shows loading initially', () => {
    renderWithProviders(<EditVehiclePage />, '/vehicles/550e8400-e29b-41d4-a716-446655440000/edit');
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  it('renders edit form after loading', async () => {
    renderWithProviders(<EditVehiclePage />, '/vehicles/550e8400-e29b-41d4-a716-446655440000/edit');
    await waitFor(() => {
      expect(screen.getByText('Edit Vehicle')).toBeInTheDocument();
    });
    expect(screen.getByRole('button', { name: /update vehicle/i })).toBeInTheDocument();
  });
});
