import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import VehicleFilters from '../VehicleFilters';

describe('VehicleFilters', () => {
  it('renders search input', () => {
    render(<VehicleFilters onFilter={vi.fn()} />);
    expect(screen.getByRole('searchbox')).toBeInTheDocument();
  });

  it('renders category dropdown', () => {
    render(<VehicleFilters onFilter={vi.fn()} />);
    expect(screen.getByLabelText('Category')).toBeInTheDocument();
  });

  it('renders search button', () => {
    render(<VehicleFilters onFilter={vi.fn()} />);
    expect(screen.getByRole('button', { name: /search/i })).toBeInTheDocument();
  });

  it('renders clear button', () => {
    render(<VehicleFilters onFilter={vi.fn()} />);
    expect(screen.getByRole('button', { name: /clear/i })).toBeInTheDocument();
  });

  it('calls onFilter with search text on search click', async () => {
    const user = userEvent.setup();
    const onFilter = vi.fn();
    render(<VehicleFilters onFilter={onFilter} />);
    await user.type(screen.getByRole('searchbox'), 'Toyota');
    await user.click(screen.getByRole('button', { name: /search/i }));
    expect(onFilter).toHaveBeenCalledWith({ make: 'Toyota', category: '' });
  });

  it('calls onFilter on category change', async () => {
    const user = userEvent.setup();
    const onFilter = vi.fn();
    render(<VehicleFilters onFilter={onFilter} />);
    await user.selectOptions(screen.getByLabelText('Category'), 'SUV');
    expect(onFilter).toHaveBeenCalledWith({ make: '', category: 'SUV' });
  });

  it('clears filters on clear click', async () => {
    const user = userEvent.setup();
    const onFilter = vi.fn();
    render(<VehicleFilters onFilter={onFilter} />);
    await user.type(screen.getByRole('searchbox'), 'Toyota');
    const clearButtons = screen.getAllByRole('button', { name: /clear/i });
    await user.click(clearButtons[clearButtons.length - 1]);
    expect(onFilter).toHaveBeenCalledWith({ make: '', category: '' });
    expect(screen.getByRole('searchbox')).toHaveValue('');
  });
});
