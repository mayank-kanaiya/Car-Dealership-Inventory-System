import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import VehicleFilters from '../VehicleFilters';

describe('VehicleFilters', () => {
  it('renders search input', () => {
    render(<VehicleFilters onFilter={vi.fn()} />);
    expect(screen.getByLabelText('Search')).toBeInTheDocument();
  });

  it('renders category dropdown', () => {
    render(<VehicleFilters onFilter={vi.fn()} />);
    expect(screen.getByLabelText('Category')).toBeInTheDocument();
  });

  it('renders search button', () => {
    render(<VehicleFilters onFilter={vi.fn()} />);
    expect(screen.getByRole('button', { name: /search/i })).toBeInTheDocument();
  });

  it('inline clear icon appears when search has text', async () => {
    const user = userEvent.setup();
    render(<VehicleFilters onFilter={vi.fn()} />);
    expect(screen.queryByRole('button', { name: /clear search/i })).not.toBeInTheDocument();
    await user.type(screen.getByLabelText('Search'), 'Toyota');
    expect(screen.getByRole('button', { name: /clear search/i })).toBeInTheDocument();
  });

  it('calls onFilter with search text on search click', async () => {
    const user = userEvent.setup();
    const onFilter = vi.fn();
    render(<VehicleFilters onFilter={onFilter} />);
    await user.type(screen.getByLabelText('Search'), 'Toyota');
    await user.click(screen.getByRole('button', { name: /^search$/i }));
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
    await user.type(screen.getByLabelText('Search'), 'Toyota');
    await user.click(screen.getByRole('button', { name: /clear search/i }));
    expect(onFilter).toHaveBeenCalledWith({ make: '', category: '' });
    expect(screen.getByLabelText('Search')).toHaveValue('');
  });
});
