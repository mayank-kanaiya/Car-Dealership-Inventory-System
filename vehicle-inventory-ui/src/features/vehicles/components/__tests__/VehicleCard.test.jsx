import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import VehicleCard from '../VehicleCard';

const mockVehicle = {
  id: '550e8400-e29b-41d4-a716-446655440000',
  make: 'Toyota',
  model: 'Camry',
  category: 'SEDAN',
  price: 28500,
  quantityInStock: 15,
  imageUrl: 'https://example.com/camry.jpg',
};

function renderVehicleCard(vehicle = mockVehicle) {
  return render(
    <MemoryRouter>
      <VehicleCard vehicle={vehicle} />
    </MemoryRouter>
  );
}

describe('VehicleCard', () => {
  it('renders vehicle make and model', () => {
    renderVehicleCard();
    expect(screen.getByText('Toyota Camry')).toBeInTheDocument();
  });

  it('renders formatted price', () => {
    renderVehicleCard();
    expect(screen.getByText('$28,500.00')).toBeInTheDocument();
  });

  it('renders category badge', () => {
    renderVehicleCard();
    expect(screen.getByText('Sedan')).toBeInTheDocument();
  });

  it('shows in stock when quantity > 0', () => {
    renderVehicleCard();
    expect(screen.getByText('15 in stock')).toBeInTheDocument();
  });

  it('shows out of stock when quantity is 0', () => {
    renderVehicleCard({ ...mockVehicle, quantityInStock: 0 });
    expect(screen.getByText('Out of stock')).toBeInTheDocument();
  });

  it('renders vehicle image with alt text', () => {
    renderVehicleCard();
    const img = screen.getByRole('img', { name: /toyota camry/i });
    expect(img).toBeInTheDocument();
    expect(img).toHaveAttribute('src', 'https://example.com/camry.jpg');
  });

  it('has link role and is keyboard accessible', () => {
    renderVehicleCard();
    const card = screen.getByRole('link', { name: /toyota camry/i });
    expect(card).toBeInTheDocument();
    expect(card).toHaveAttribute('tabIndex', '0');
  });

  it('shows image placeholder on error', async () => {
    renderVehicleCard();
    const img = screen.getByRole('img', { name: /toyota camry/i });
    fireEvent.error(img);
    expect(await screen.findByText('No image')).toBeInTheDocument();
  });
});
