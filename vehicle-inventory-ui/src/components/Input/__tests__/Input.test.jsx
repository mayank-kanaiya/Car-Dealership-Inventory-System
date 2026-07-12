import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import Input from '../Input';

describe('Input Component', () => {
  it('renders with label', () => {
    render(<Input label="Email" />);
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
  });

  it('renders with placeholder', () => {
    render(<Input placeholder="Enter email" />);
    expect(screen.getByPlaceholderText(/enter email/i)).toBeInTheDocument();
  });

  it('displays error message', () => {
    render(<Input label="Email" error="Email is required" />);
    expect(screen.getByText(/email is required/i)).toBeInTheDocument();
  });

  it('shows required indicator when required prop is true', () => {
    render(<Input label="Name" required />);
    expect(screen.getByText('*')).toBeInTheDocument();
  });

  it('displays helper text', () => {
    render(<Input helperText="Must be a valid email" />);
    expect(screen.getByText(/must be a valid email/i)).toBeInTheDocument();
  });

  it('updates value on change', async () => {
    const user = userEvent.setup();
    render(<Input label="Name" />);
    const input = screen.getByLabelText(/name/i);
    await user.type(input, 'John');
    expect(input).toHaveValue('John');
  });

  it('renders disabled state', () => {
    render(<Input label="Name" disabled />);
    expect(screen.getByLabelText(/name/i)).toBeDisabled();
  });

  it('renders password type', () => {
    render(<Input label="Password" type="password" />);
    expect(screen.getByLabelText(/password/i)).toHaveAttribute('type', 'password');
  });

  it('applies error border styles', () => {
    render(<Input label="Email" error="Error" />);
    const input = screen.getByLabelText(/email/i);
    expect(input.className).toContain('border-danger');
  });

  it('forwards ref', () => {
    const ref = { current: null };
    render(<Input label="Name" ref={ref} />);
    expect(ref.current).toBeInstanceOf(HTMLInputElement);
  });
});
