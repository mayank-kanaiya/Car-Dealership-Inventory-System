import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import Badge from '../Badge';

describe('Badge Component', () => {
  it('renders with text content', () => {
    render(<Badge>In Stock</Badge>);
    expect(screen.getByText(/in stock/i)).toBeInTheDocument();
  });

  it('renders default variant', () => {
    render(<Badge>Default</Badge>);
    const badge = screen.getByText(/default/i);
    expect(badge.className).toContain('bg-gray-100');
  });

  it('renders success variant', () => {
    render(<Badge variant="success">Active</Badge>);
    const badge = screen.getByText(/active/i);
    expect(badge.className).toContain('bg-green-100');
  });

  it('renders danger variant', () => {
    render(<Badge variant="danger">Error</Badge>);
    const badge = screen.getByText(/error/i);
    expect(badge.className).toContain('bg-red-100');
  });

  it('renders warning variant', () => {
    render(<Badge variant="warning">Pending</Badge>);
    const badge = screen.getByText(/pending/i);
    expect(badge.className).toContain('bg-amber-100');
  });

  it('renders primary variant', () => {
    render(<Badge variant="primary">Admin</Badge>);
    const badge = screen.getByText(/admin/i);
    expect(badge.className).toContain('bg-primary');
  });

  it('renders info variant', () => {
    render(<Badge variant="info">Info</Badge>);
    const badge = screen.getByText(/info/i);
    expect(badge.className).toContain('bg-blue-100');
  });

  it('renders small size', () => {
    render(<Badge size="sm">Small</Badge>);
    const badge = screen.getByText(/small/i);
    expect(badge.className).toContain('px-2');
  });

  it('accepts custom className', () => {
    render(<Badge className="custom">Custom</Badge>);
    expect(screen.getByText(/custom/i).className).toContain('custom');
  });
});
