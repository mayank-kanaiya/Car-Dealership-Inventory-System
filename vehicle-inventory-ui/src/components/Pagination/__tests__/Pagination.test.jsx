import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import Pagination from '../Pagination';

describe('Pagination Component', () => {
  const defaultProps = {
    currentPage: 1,
    totalPages: 5,
    onPageChange: vi.fn(),
  };

  it('renders page numbers', () => {
    render(<Pagination {...defaultProps} />);
    expect(screen.getByRole('button', { name: /1/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /5/i })).toBeInTheDocument();
  });

  it('highlights current page', () => {
    render(<Pagination {...defaultProps} currentPage={3} />);
    const currentPageBtn = screen.getByRole('button', { name: /3/i });
    expect(currentPageBtn).toHaveAttribute('aria-current', 'page');
  });

  it('calls onPageChange when page is clicked', async () => {
    const user = userEvent.setup();
    const onPageChange = vi.fn();
    render(<Pagination {...defaultProps} onPageChange={onPageChange} />);

    await user.click(screen.getByRole('button', { name: /3/i }));
    expect(onPageChange).toHaveBeenCalledWith(3);
  });

  it('disables previous button on first page', () => {
    render(<Pagination {...defaultProps} currentPage={1} />);
    expect(screen.getByRole('button', { name: /previous/i })).toBeDisabled();
  });

  it('disables next button on last page', () => {
    render(<Pagination {...defaultProps} currentPage={5} />);
    expect(screen.getByRole('button', { name: /next/i })).toBeDisabled();
  });

  it('navigates to previous page', async () => {
    const user = userEvent.setup();
    const onPageChange = vi.fn();
    render(<Pagination {...defaultProps} currentPage={3} onPageChange={onPageChange} />);

    await user.click(screen.getByRole('button', { name: /previous/i }));
    expect(onPageChange).toHaveBeenCalledWith(2);
  });

  it('navigates to next page', async () => {
    const user = userEvent.setup();
    const onPageChange = vi.fn();
    render(<Pagination {...defaultProps} currentPage={3} onPageChange={onPageChange} />);

    await user.click(screen.getByRole('button', { name: /next/i }));
    expect(onPageChange).toHaveBeenCalledWith(4);
  });

  it('does not render when totalPages is 1', () => {
    const { container } = render(<Pagination {...defaultProps} totalPages={1} />);
    expect(container.firstChild).toBeNull();
  });
});
