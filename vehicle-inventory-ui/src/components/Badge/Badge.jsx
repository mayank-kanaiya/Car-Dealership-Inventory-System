import PropTypes from 'prop-types';

const variantStyles = {
  default: 'bg-gray-100 text-text-secondary',
  primary: 'bg-primary-light text-primary',
  success: 'bg-green-100 text-success',
  danger: 'bg-red-100 text-danger',
  warning: 'bg-amber-100 text-warning',
  info: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
};

const sizeStyles = {
  sm: 'px-2 py-0.5 text-xs',
  md: 'px-2.5 py-0.5 text-xs',
  lg: 'px-3 py-1 text-sm',
};

function Badge({ children, variant = 'default', size = 'md', className = '' }) {
  return (
    <span
      className={`inline-flex items-center font-medium rounded-full ${variantStyles[variant]} ${sizeStyles[size]} ${className}`}
    >
      {children}
    </span>
  );
}

Badge.propTypes = {
  children: PropTypes.node.isRequired,
  variant: PropTypes.oneOf(['default', 'primary', 'success', 'danger', 'warning', 'info']),
  size: PropTypes.oneOf(['sm', 'md', 'lg']),
  className: PropTypes.string,
};

export default Badge;
