import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import SearchBar from '../SearchBar';

describe('SearchBar Component', () => {
  it('renders search input', () => {
    render(<SearchBar value="" onChange={vi.fn()} />);
    expect(screen.getByRole('searchbox')).toBeInTheDocument();
  });

  it('renders with placeholder', () => {
    render(<SearchBar value="" onChange={vi.fn()} placeholder="Search vehicles" />);
    expect(screen.getByPlaceholderText(/search vehicles/i)).toBeInTheDocument();
  });

  it('displays current value', () => {
    render(<SearchBar value="Tesla" onChange={vi.fn()} />);
    expect(screen.getByRole('searchbox')).toHaveValue('Tesla');
  });

  it('calls onChange when typing', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    render(<SearchBar value="" onChange={onChange} />);

    await user.type(screen.getByRole('searchbox'), 'BMW');
    expect(onChange).toHaveBeenCalledTimes(3);
  });

  it('renders clear button when value is not empty', () => {
    render(<SearchBar value="Tesla" onChange={vi.fn()} />);
    expect(screen.getByRole('button', { name: /clear/i })).toBeInTheDocument();
  });

  it('does not render clear button when value is empty', () => {
    render(<SearchBar value="" onChange={vi.fn()} />);
    expect(screen.queryByRole('button', { name: /clear/i })).not.toBeInTheDocument();
  });

  it('calls onChange with empty string when clear is clicked', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    render(<SearchBar value="Tesla" onChange={onChange} />);

    await user.click(screen.getByRole('button', { name: /clear/i }));
    expect(onChange).toHaveBeenCalledWith('');
  });

  it('renders search icon', () => {
    render(<SearchBar value="" onChange={vi.fn()} />);
    expect(screen.getByTestId('search-icon')).toBeInTheDocument();
  });
});
