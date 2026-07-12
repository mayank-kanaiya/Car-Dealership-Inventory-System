import { useEffect, useCallback, useRef } from 'react';
import PropTypes from 'prop-types';
import { X } from 'lucide-react';

const sizeStyles = {
  sm: 'max-w-sm',
  md: 'max-w-md',
  lg: 'max-w-lg',
  xl: 'max-w-xl',
  full: 'max-w-3xl',
};

function Modal({ isOpen, onClose, title, children, size = 'md' }) {
  const dialogRef = useRef(null);
  const previousFocusRef = useRef(null);

  const handleEscape = useCallback(
    (e) => {
      if (e.key === 'Escape') onClose();
    },
    [onClose]
  );

  const handleTabTrap = useCallback((e) => {
    if (e.key !== 'Tab' || !dialogRef.current) return;
    const focusable = dialogRef.current.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    );
    if (focusable.length === 0) return;
    const first = focusable[0];
    const last = focusable[focusable.length - 1];
    if (e.shiftKey) {
      if (document.activeElement === first) {
        e.preventDefault();
        last.focus();
      }
    } else {
      if (document.activeElement === last) {
        e.preventDefault();
        first.focus();
      }
    }
  }, []);

  useEffect(() => {
    if (isOpen) {
      previousFocusRef.current = document.activeElement;
      document.addEventListener('keydown', handleEscape);
      document.addEventListener('keydown', handleTabTrap);
      document.body.style.overflow = 'hidden';
      // eslint-disable-next-line no-undef
      requestAnimationFrame(() => {
        if (dialogRef.current) {
          const firstInput = dialogRef.current.querySelector(
            'input, select, textarea, button:not([aria-label="Close"])'
          );
          if (firstInput) firstInput.focus();
        }
      });
    }
    return () => {
      document.removeEventListener('keydown', handleEscape);
      document.removeEventListener('keydown', handleTabTrap);
      document.body.style.overflow = '';
      if (previousFocusRef.current && previousFocusRef.current.focus) {
        previousFocusRef.current.focus();
      }
    };
  }, [isOpen, handleEscape, handleTabTrap]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div
        data-testid="modal-overlay"
        className="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity"
        onClick={onClose}
      />
      <div
        ref={dialogRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-title"
        className={`relative z-10 w-full ${sizeStyles[size]} mx-4 bg-surface rounded-2xl shadow-2xl border border-border animate-in fade-in zoom-in-95 duration-200`}
      >
        <div className="flex items-center justify-between px-6 py-4 border-b border-border">
          <h2 id="modal-title" className="text-lg font-semibold text-text-primary">
            {title}
          </h2>
          <button
            type="button"
            onClick={onClose}
            aria-label="Close"
            className="p-1.5 rounded-lg text-text-muted hover:text-text-primary hover:bg-surface-hover transition-colors cursor-pointer"
          >
            <X size={18} />
          </button>
        </div>
        <div role="document" className="px-6 py-5">
          {children}
        </div>
      </div>
    </div>
  );
}

Modal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  title: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
  size: PropTypes.oneOf(['sm', 'md', 'lg', 'xl', 'full']),
};

export default Modal;
