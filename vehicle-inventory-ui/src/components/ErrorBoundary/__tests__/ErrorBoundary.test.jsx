import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import ErrorBoundary from '../ErrorBoundary';

function ThrowingComponent({ shouldThrow = true }) {
  if (shouldThrow) {
    throw new Error('Test error');
  }
  return <div>Child content</div>;
}

describe('ErrorBoundary', () => {
  const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

  afterEach(() => {
    consoleSpy.mockClear();
  });

  it('renders children when no error occurs', () => {
    render(
      <ErrorBoundary>
        <ThrowingComponent shouldThrow={false} />
      </ErrorBoundary>
    );
    expect(screen.getByText(/child content/i)).toBeInTheDocument();
  });

  it('renders fallback UI when an error occurs', () => {
    render(
      <ErrorBoundary>
        <ThrowingComponent />
      </ErrorBoundary>
    );
    expect(screen.getByText(/something went wrong/i)).toBeInTheDocument();
    expect(screen.queryByText(/child content/i)).not.toBeInTheDocument();
  });

  it('displays error message in fallback', () => {
    render(
      <ErrorBoundary>
        <ThrowingComponent />
      </ErrorBoundary>
    );
    expect(screen.getByText(/test error/i)).toBeInTheDocument();
  });

  it('provides a retry button that resets the error', async () => {
    const { rerender } = render(
      <ErrorBoundary>
        <ThrowingComponent shouldThrow />
      </ErrorBoundary>
    );
    expect(screen.getByText(/something went wrong/i)).toBeInTheDocument();

    rerender(
      <ErrorBoundary>
        <ThrowingComponent shouldThrow={false} />
      </ErrorBoundary>
    );
    const retryButton = screen.getByRole('button', { name: /try again/i });
    retryButton.click();
    await waitFor(() => {
      expect(screen.getByText(/child content/i)).toBeInTheDocument();
    });
  });

  it('logs error to console', () => {
    render(
      <ErrorBoundary>
        <ThrowingComponent />
      </ErrorBoundary>
    );
    expect(consoleSpy).toHaveBeenCalled();
  });
});
