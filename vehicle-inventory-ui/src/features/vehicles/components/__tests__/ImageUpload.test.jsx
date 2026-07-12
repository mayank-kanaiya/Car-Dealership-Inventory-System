import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import ImageUpload from '../ImageUpload';

describe('ImageUpload', () => {
  it('renders upload area when no image', () => {
    render(<ImageUpload onFileSelect={vi.fn()} />);
    expect(screen.getByRole('button', { name: /upload image/i })).toBeInTheDocument();
  });

  it('renders preview when currentImageUrl provided', () => {
    render(<ImageUpload currentImageUrl="https://example.com/car.jpg" onFileSelect={vi.fn()} />);
    expect(screen.getByRole('img', { name: /preview/i })).toHaveAttribute('src', 'https://example.com/car.jpg');
  });

  it('calls onFileSelect when file chosen', () => {
    const onFileSelect = vi.fn();
    render(<ImageUpload onFileSelect={onFileSelect} />);
    const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
    const input = screen.getByLabelText(/file input/i);
    fireEvent.change(input, { target: { files: [file] } });
    expect(onFileSelect).toHaveBeenCalledWith(file);
  });

  it('calls onRemove when remove button clicked', () => {
    const onRemove = vi.fn();
    render(<ImageUpload currentImageUrl="https://example.com/car.jpg" onFileSelect={vi.fn()} onRemove={onRemove} />);
    fireEvent.click(screen.getByRole('button', { name: /remove image/i }));
    expect(onRemove).toHaveBeenCalled();
  });

  it('renders Choose File button when no image', () => {
    render(<ImageUpload onFileSelect={vi.fn()} />);
    expect(screen.getByRole('button', { name: /choose file/i })).toBeInTheDocument();
  });

  it('disables file input when disabled', () => {
    render(<ImageUpload onFileSelect={vi.fn()} disabled />);
    const input = screen.getByLabelText(/file input/i);
    expect(input).toBeDisabled();
  });
});
