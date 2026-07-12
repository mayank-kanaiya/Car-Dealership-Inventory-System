import { forwardRef } from 'react';
import PropTypes from 'prop-types';

const Input = forwardRef(function Input(
  {
    label,
    type = 'text',
    error,
    helperText,
    required = false,
    disabled = false,
    placeholder,
    className = '',
    ...rest
  },
  ref
) {
  const inputId = label ? label.toLowerCase().replace(/\s+/g, '-') : rest.id;

  const baseInputStyles =
    'w-full px-3.5 py-2.5 text-sm border rounded-xl bg-surface text-text-primary placeholder-text-muted focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary disabled:bg-surface-secondary disabled:cursor-not-allowed transition-all duration-150';

  const borderStyle = error
    ? 'border-danger focus:ring-danger focus:border-danger'
    : 'border-border hover:border-gray-300 dark:hover:border-gray-600';

  return (
    <div className={`flex flex-col gap-1.5 ${className}`}>
      {label && (
        <label htmlFor={inputId} className="text-sm font-medium text-text-primary">
          {label}
          {required && <span className="text-danger ml-0.5">*</span>}
        </label>
      )}
      <input
        ref={ref}
        id={inputId}
        type={type}
        placeholder={placeholder}
        disabled={disabled}
        aria-invalid={!!error}
        aria-describedby={error ? `${inputId}-error` : helperText ? `${inputId}-helper` : undefined}
        className={`${baseInputStyles} ${borderStyle}`}
        {...rest}
      />
      {error && (
        <p
          id={`${inputId}-error`}
          className="text-xs text-danger flex items-center gap-1"
          role="alert"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="12"
            height="12"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <circle cx="12" cy="12" r="10" />
            <line x1="12" x2="12" y1="8" y2="12" />
            <line x1="12" x2="12.01" y1="16" y2="16" />
          </svg>
          {error}
        </p>
      )}
      {!error && helperText && (
        <p id={`${inputId}-helper`} className="text-xs text-text-muted">
          {helperText}
        </p>
      )}
    </div>
  );
});

Input.propTypes = {
  label: PropTypes.string,
  type: PropTypes.string,
  error: PropTypes.string,
  helperText: PropTypes.string,
  required: PropTypes.bool,
  disabled: PropTypes.bool,
  placeholder: PropTypes.string,
  className: PropTypes.string,
};

Input.displayName = 'Input';

export default Input;
