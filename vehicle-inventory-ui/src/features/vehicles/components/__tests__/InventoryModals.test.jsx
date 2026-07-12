import { render, screen, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import { PurchaseModal, RestockModal } from '../InventoryModals';

const mockVehicle = {
  id: '1',
  make: 'Toyota',
  model: 'Camry',
  quantityInStock: 15,
};

describe('PurchaseModal', () => {
  it('renders with vehicle info', () => {
    render(<PurchaseModal vehicle={mockVehicle} isOpen onClose={vi.fn()} onConfirm={vi.fn()} />);
    expect(screen.getByText(/purchase toyota camry/i)).toBeInTheDocument();
    expect(screen.getByText('15')).toBeInTheDocument();
  });

  it('shows validation error for invalid quantity', async () => {
    render(<PurchaseModal vehicle={mockVehicle} isOpen onClose={vi.fn()} onConfirm={vi.fn()} />);
    fireEvent.change(screen.getByLabelText(/quantity/i), { target: { value: '' } });
    fireEvent.click(screen.getByRole('button', { name: /confirm purchase/i }));
    expect(screen.getByText(/quantity must be at least 1/i)).toBeInTheDocument();
  });

  it('shows error when quantity exceeds stock', async () => {
    const user = userEvent.setup();
    render(<PurchaseModal vehicle={mockVehicle} isOpen onClose={vi.fn()} onConfirm={vi.fn()} />);
    const input = screen.getByLabelText(/quantity/i);
    await user.clear(input);
    await user.type(input, '20');
    fireEvent.submit(input.closest('form'));
    expect(screen.getByText(/only 15 units available/i)).toBeInTheDocument();
  });

  it('calls onConfirm with valid quantity', async () => {
    const user = userEvent.setup();
    const onConfirm = vi.fn();
    render(<PurchaseModal vehicle={mockVehicle} isOpen onClose={vi.fn()} onConfirm={onConfirm} />);
    const input = screen.getByLabelText(/quantity/i);
    await user.clear(input);
    await user.type(input, '3');
    await user.click(screen.getByRole('button', { name: /confirm purchase/i }));
    expect(onConfirm).toHaveBeenCalledWith('1', 3);
  });

  it('calls onClose when cancel clicked', () => {
    const onClose = vi.fn();
    render(<PurchaseModal vehicle={mockVehicle} isOpen onClose={onClose} onConfirm={vi.fn()} />);
    fireEvent.click(screen.getByRole('button', { name: /cancel/i }));
    expect(onClose).toHaveBeenCalled();
  });
});

describe('RestockModal', () => {
  it('renders with vehicle info', () => {
    render(<RestockModal vehicle={mockVehicle} isOpen onClose={vi.fn()} onConfirm={vi.fn()} />);
    expect(screen.getByText(/restock toyota camry/i)).toBeInTheDocument();
  });

  it('calls onConfirm with quantity', () => {
    const onConfirm = vi.fn();
    render(<RestockModal vehicle={mockVehicle} isOpen onClose={vi.fn()} onConfirm={onConfirm} />);
    fireEvent.change(screen.getByLabelText(/quantity to add/i), { target: { value: '20' } });
    fireEvent.click(screen.getByRole('button', { name: /confirm restock/i }));
    expect(onConfirm).toHaveBeenCalledWith('1', 20);
  });

  it('shows error for invalid quantity', () => {
    render(<RestockModal vehicle={mockVehicle} isOpen onClose={vi.fn()} onConfirm={vi.fn()} />);
    fireEvent.change(screen.getByLabelText(/quantity to add/i), { target: { value: '' } });
    fireEvent.click(screen.getByRole('button', { name: /confirm restock/i }));
    expect(screen.getByText(/quantity must be at least 1/i)).toBeInTheDocument();
  });
});
