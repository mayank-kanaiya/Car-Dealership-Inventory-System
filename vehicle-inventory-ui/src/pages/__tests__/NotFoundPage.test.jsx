import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import NotFoundPage from '../NotFoundPage';

function renderWithRouter(route = '/nonexistent') {
  return render(
    <MemoryRouter initialEntries={[route]}>
      <NotFoundPage />
    </MemoryRouter>
  );
}

describe('NotFoundPage', () => {
  it('renders 404 heading', () => {
    renderWithRouter();
    expect(screen.getByText(/404/i)).toBeInTheDocument();
  });

  it('renders page not found message', () => {
    renderWithRouter();
    expect(screen.getByText(/page not found/i)).toBeInTheDocument();
  });

  it('provides a link back to vehicles', () => {
    renderWithRouter();
    expect(screen.getByRole('link', { name: /back to inventory/i })).toHaveAttribute(
      'href',
      '/vehicles'
    );
  });
});
