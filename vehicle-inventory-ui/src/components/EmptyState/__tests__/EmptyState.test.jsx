import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import EmptyState from '../EmptyState';

describe('EmptyState Component', () => {
  it('renders title', () => {
    render(<EmptyState title="No vehicles found" />);
    expect(screen.getByText(/no vehicles found/i)).toBeInTheDocument();
  });

  it('renders description', () => {
    render(<EmptyState title="Empty" description="Add some vehicles to get started" />);
    expect(screen.getByText(/add some vehicles/i)).toBeInTheDocument();
  });

  it('renders action button when onAction is provided', () => {
    render(<EmptyState title="Empty" actionLabel="Add Vehicle" onAction={vi.fn()} />);
    expect(screen.getByRole('button', { name: /add vehicle/i })).toBeInTheDocument();
  });

  it('does not render action button when onAction is not provided', () => {
    render(<EmptyState title="Empty" actionLabel="Add" />);
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });

  it('calls onAction when action button is clicked', async () => {
    const { default: userEvent } = await import('@testing-library/user-event');
    const onAction = vi.fn();
    render(<EmptyState title="Empty" actionLabel="Add" onAction={onAction} />);

    await userEvent.setup().click(screen.getByRole('button', { name: /add/i }));
    expect(onAction).toHaveBeenCalledTimes(1);
  });

  it('renders custom icon', () => {
    render(<EmptyState title="Empty" icon={<span data-testid="custom-icon">📦</span>} />);
    expect(screen.getByTestId('custom-icon')).toBeInTheDocument();
  });
});
