import { forwardRef } from 'react';
import PropTypes from 'prop-types';

const Input = forwardRef(function Input(
  { label, type = 'text', error, helperText, required = false, disabled = false, placeholder, className = '', ...rest },
  ref
) {
  const inputId = label ? label.toLowerCase().replace(/\s+/g, '-') : rest.id;

  const baseInputStyles =
    'w-full px-3 py-2 text-sm border rounded-lg bg-surface text-text-primary placeholder-text-muted focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary disabled:bg-gray-50 disabled:cursor-not-allowed transition-colors duration-200';

  const borderStyle = error ? 'border-danger focus:ring-danger focus:border-danger' : 'border-border';

  return (
    <div className={`flex flex-col gap-1 ${className}`}>
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
        <p id={`${inputId}-error`} className="text-xs text-danger" role="alert">
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
