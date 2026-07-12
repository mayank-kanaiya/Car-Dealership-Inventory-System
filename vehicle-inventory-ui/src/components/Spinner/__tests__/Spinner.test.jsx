import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import Spinner from '../Spinner';

describe('Spinner Component', () => {
  it('renders a loading indicator', () => {
    render(<Spinner />);
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  it('has accessible text', () => {
    render(<Spinner />);
    expect(screen.getByText(/loading/i)).toBeInTheDocument();
  });

  it('renders with default size', () => {
    render(<Spinner />);
    const spinner = screen.getByRole('status');
    expect(spinner.className).toContain('h-6');
  });

  it('renders with small size', () => {
    render(<Spinner size="sm" />);
    const spinner = screen.getByRole('status');
    expect(spinner.className).toContain('h-4');
  });

  it('renders with large size', () => {
    render(<Spinner size="lg" />);
    const spinner = screen.getByRole('status');
    expect(spinner.className).toContain('h-10');
  });

  it('accepts custom className', () => {
    render(<Spinner className="my-custom-class" />);
    expect(screen.getByRole('status').className).toContain('my-custom-class');
  });
});
