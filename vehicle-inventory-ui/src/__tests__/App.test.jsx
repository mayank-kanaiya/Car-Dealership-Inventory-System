import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import App from '../App';

describe('App Component', () => {
  it('renders 404 page for unknown routes', () => {
    render(<App />);
    expect(screen.getByText(/404/i)).toBeInTheDocument();
    expect(screen.getByText(/page not found/i)).toBeInTheDocument();
  });
});
