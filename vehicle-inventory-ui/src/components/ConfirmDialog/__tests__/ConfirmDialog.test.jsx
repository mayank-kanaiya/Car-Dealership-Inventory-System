import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import ConfirmDialog from '../ConfirmDialog';

describe('ConfirmDialog Component', () => {
  it('renders when isOpen is true', () => {
    render(
      <ConfirmDialog
        isOpen={true}
        onConfirm={vi.fn()}
        onCancel={vi.fn()}
        title="Delete Vehicle"
        message="Are you sure?"
      />
    );
    expect(screen.getByText(/delete vehicle/i)).toBeInTheDocument();
    expect(screen.getByText(/are you sure\?/i)).toBeInTheDocument();
  });

  it('does not render when isOpen is false', () => {
    render(
      <ConfirmDialog
        isOpen={false}
        onConfirm={vi.fn()}
        onCancel={vi.fn()}
        title="Delete"
        message="Sure?"
      />
    );
    expect(screen.queryByText(/delete/i)).not.toBeInTheDocument();
  });

  it('renders confirm and cancel buttons', () => {
    render(
      <ConfirmDialog
        isOpen={true}
        onConfirm={vi.fn()}
        onCancel={vi.fn()}
        title="Delete"
        message="Sure?"
        confirmLabel="Yes, delete"
        cancelLabel="No, keep"
      />
    );
    expect(screen.getByRole('button', { name: /yes, delete/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /no, keep/i })).toBeInTheDocument();
  });

  it('calls onConfirm when confirm button is clicked', async () => {
    const user = userEvent.setup();
    const onConfirm = vi.fn();
    render(
      <ConfirmDialog
        isOpen={true}
        onConfirm={onConfirm}
        onCancel={vi.fn()}
        title="Delete"
        message="Sure?"
      />
    );

    await user.click(screen.getByRole('button', { name: /confirm/i }));
    expect(onConfirm).toHaveBeenCalledTimes(1);
  });

  it('calls onCancel when cancel button is clicked', async () => {
    const user = userEvent.setup();
    const onCancel = vi.fn();
    render(
      <ConfirmDialog
        isOpen={true}
        onConfirm={vi.fn()}
        onCancel={onCancel}
        title="Delete"
        message="Sure?"
      />
    );

    await user.click(screen.getByRole('button', { name: /cancel/i }));
    expect(onCancel).toHaveBeenCalledTimes(1);
  });

  it('renders danger variant for destructive actions', () => {
    render(
      <ConfirmDialog
        isOpen={true}
        onConfirm={vi.fn()}
        onCancel={vi.fn()}
        title="Delete"
        message="Sure?"
        variant="danger"
      />
    );
    const confirmBtn = screen.getByRole('button', { name: /confirm/i });
    expect(confirmBtn.className).toContain('bg-danger');
  });
});
