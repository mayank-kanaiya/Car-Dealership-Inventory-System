import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import Skeleton from '../Skeleton';

describe('Skeleton Component', () => {
  it('renders a skeleton element', () => {
    render(<Skeleton />);
    expect(screen.getByTestId('skeleton')).toBeInTheDocument();
  });

  it('renders with default height', () => {
    render(<Skeleton />);
    const skeleton = screen.getByTestId('skeleton');
    expect(skeleton.className).toContain('h-4');
  });

  it('renders with custom height via className', () => {
    render(<Skeleton className="h-20" />);
    expect(screen.getByTestId('skeleton').className).toContain('h-20');
  });

  it('renders full width by default', () => {
    render(<Skeleton />);
    expect(screen.getByTestId('skeleton').className).toContain('w-full');
  });

  it('renders with rounded corners', () => {
    render(<Skeleton />);
    expect(screen.getByTestId('skeleton').className).toContain('rounded');
  });

  it('renders multiple skeletons', () => {
    render(
      <div>
        <Skeleton />
        <Skeleton />
        <Skeleton />
      </div>
    );
    const skeletons = screen.getAllByTestId('skeleton');
    expect(skeletons).toHaveLength(3);
  });

  it('accepts custom className', () => {
    render(<Skeleton className="w-1/2 h-8" />);
    const skeleton = screen.getByTestId('skeleton');
    expect(skeleton.className).toContain('w-1/2');
    expect(skeleton.className).toContain('h-8');
  });
});
