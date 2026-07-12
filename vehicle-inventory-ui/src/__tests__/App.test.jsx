import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import App from '../App';

describe('App Component', () => {
  it('renders without crashing and redirects to login', () => {
    render(<App />);
    expect(screen.getByText(/vehicle inventory/i)).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: /sign in/i })).toBeInTheDocument();
  });
});
