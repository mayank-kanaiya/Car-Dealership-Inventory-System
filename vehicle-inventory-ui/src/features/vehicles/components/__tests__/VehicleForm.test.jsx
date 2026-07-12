import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import VehicleForm from '../VehicleForm';

function renderForm(props = {}) {
  const defaultProps = {
    onSubmit: vi.fn(),
    onCancel: vi.fn(),
    ...props,
  };
  return render(
    <MemoryRouter>
      <VehicleForm {...defaultProps} />
    </MemoryRouter>
  );
}

describe('VehicleForm', () => {
  it('renders all form fields', () => {
    renderForm();
    expect(screen.getByLabelText(/make/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/model/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/category/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/price/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/quantity in stock/i)).toBeInTheDocument();
  });

  it('renders create button when no vehicle provided', () => {
    renderForm();
    expect(screen.getByRole('button', { name: /create vehicle/i })).toBeInTheDocument();
  });

  it('renders update button when vehicle provided', () => {
    renderForm({ vehicle: { id: '1', make: 'Toyota', model: 'Camry', category: 'SEDAN', price: 28500, quantityInStock: 10 } });
    expect(screen.getByRole('button', { name: /update vehicle/i })).toBeInTheDocument();
  });

  it('shows validation errors on empty submit', async () => {
    const user = userEvent.setup();
    renderForm();
    await user.click(screen.getByRole('button', { name: /create vehicle/i }));
    expect(await screen.findByText(/make is required/i)).toBeInTheDocument();
    expect(screen.getByText(/model is required/i)).toBeInTheDocument();
    expect(screen.getByText(/category is required|invalid category/i)).toBeInTheDocument();
    expect(screen.getByText(/price is required|price must be/i)).toBeInTheDocument();
    expect(screen.getByText(/stock is required|quantityInStock must be/i)).toBeInTheDocument();
  });

  it('calls onSubmit with form data', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn();
    renderForm({ onSubmit });

    await user.type(screen.getByLabelText(/make/i), 'Tesla');
    await user.type(screen.getByLabelText(/model/i), 'Model 3');
    await user.selectOptions(screen.getByLabelText(/category/i), 'ELECTRIC_SUV');
    await user.type(screen.getByLabelText(/price/i), '42000');
    await user.type(screen.getByLabelText(/quantity in stock/i), '5');
    await user.click(screen.getByRole('button', { name: /create vehicle/i }));

    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalledTimes(1);
      const callData = onSubmit.mock.calls[0][0];
      expect(callData).toMatchObject({
        make: 'Tesla',
        model: 'Model 3',
        category: 'ELECTRIC_SUV',
        price: 42000,
        quantityInStock: 5,
      });
    });
  });

  it('calls onCancel when cancel button clicked', async () => {
    const user = userEvent.setup();
    const onCancel = vi.fn();
    renderForm({ onCancel });
    await user.click(screen.getByRole('button', { name: /cancel/i }));
    expect(onCancel).toHaveBeenCalled();
  });

  it('populates fields when editing existing vehicle', () => {
    const vehicle = { id: '1', make: 'Toyota', model: 'Camry', category: 'SEDAN', price: 28500, quantityInStock: 10 };
    renderForm({ vehicle });
    expect(screen.getByLabelText(/make/i)).toHaveValue('Toyota');
    expect(screen.getByLabelText(/model/i)).toHaveValue('Camry');
    expect(screen.getByLabelText(/category/i)).toHaveValue('SEDAN');
    expect(screen.getByLabelText(/price/i)).toHaveValue(28500);
    expect(screen.getByLabelText(/quantity in stock/i)).toHaveValue(10);
  });
});
