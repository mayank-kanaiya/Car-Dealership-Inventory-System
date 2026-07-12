import PropTypes from 'prop-types';

function Button({
  children,
  variant = 'primary',
  size = 'md',
  isLoading = false,
  disabled = false,
  fullWidth = false,
  icon = null,
  onClick,
  type = 'button',
  ...rest
}) {
  const baseStyles =
    'inline-flex items-center justify-center font-medium rounded-xl transition-all duration-150 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer active:scale-[0.98]';

  const variantStyles = {
    primary:
      'bg-primary text-white hover:bg-primary-hover focus:ring-primary shadow-sm shadow-primary/20 hover:shadow-md hover:shadow-primary/30',
    secondary:
      'bg-surface-secondary text-text-primary hover:bg-gray-200 dark:hover:bg-gray-700 focus:ring-gray-400 border border-border',
    danger:
      'bg-danger text-white hover:bg-danger-hover focus:ring-danger shadow-sm shadow-danger/20',
    ghost: 'bg-transparent text-text-primary hover:bg-surface-hover focus:ring-gray-400',
  };

  const sizeStyles = {
    sm: 'px-3 py-1.5 text-sm gap-1.5',
    md: 'px-4 py-2.5 text-sm gap-2',
    lg: 'px-6 py-3 text-base gap-2.5',
  };

  const widthStyle = fullWidth ? 'w-full' : '';

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled || isLoading}
      className={`${baseStyles} ${variantStyles[variant]} ${sizeStyles[size]} ${widthStyle}`}
      data-testid={rest['data-testid']}
      {...rest}
    >
      {isLoading ? (
        <span
          data-testid="button-spinner"
          className="animate-spin h-4 w-4 border-2 border-current border-t-transparent rounded-full"
        />
      ) : icon ? (
        <span className="shrink-0">{icon}</span>
      ) : null}
      {children}
    </button>
  );
}

Button.propTypes = {
  children: PropTypes.node.isRequired,
  variant: PropTypes.oneOf(['primary', 'secondary', 'danger', 'ghost']),
  size: PropTypes.oneOf(['sm', 'md', 'lg']),
  isLoading: PropTypes.bool,
  disabled: PropTypes.bool,
  fullWidth: PropTypes.bool,
  icon: PropTypes.node,
  onClick: PropTypes.func,
  type: PropTypes.oneOf(['button', 'submit', 'reset']),
};

Button.displayName = 'Button';

export default Button;
