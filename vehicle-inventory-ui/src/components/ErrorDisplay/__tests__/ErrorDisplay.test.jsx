import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import ErrorDisplay from '../ErrorDisplay';

describe('ErrorDisplay Component', () => {
  it('renders error message', () => {
    render(<ErrorDisplay message="Something went wrong" />);
    expect(screen.getByText(/something went wrong/i)).toBeInTheDocument();
  });

  it('renders default title', () => {
    render(<ErrorDisplay message="Error occurred" />);
    expect(screen.getByRole('heading', { name: /error/i })).toBeInTheDocument();
  });

  it('renders custom title', () => {
    render(<ErrorDisplay title="Network Error" message="Failed to fetch" />);
    expect(screen.getByRole('heading', { name: /network error/i })).toBeInTheDocument();
  });

  it('renders retry button when onRetry is provided', () => {
    render(<ErrorDisplay message="Error" onRetry={vi.fn()} />);
    expect(screen.getByRole('button', { name: /try again/i })).toBeInTheDocument();
  });

  it('calls onRetry when retry button is clicked', async () => {
    const user = userEvent.setup();
    const onRetry = vi.fn();
    render(<ErrorDisplay message="Error" onRetry={onRetry} />);

    await user.click(screen.getByRole('button', { name: /try again/i }));
    expect(onRetry).toHaveBeenCalledTimes(1);
  });

  it('does not render retry button when onRetry is not provided', () => {
    render(<ErrorDisplay message="Error" />);
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });

  it('renders error icon', () => {
    render(<ErrorDisplay message="Error" />);
    expect(screen.getByTestId('error-icon')).toBeInTheDocument();
  });
});
