import PropTypes from 'prop-types';

const sizeMap = {
  sm: 'h-4 w-4 border',
  md: 'h-6 w-6 border-2',
  lg: 'h-10 w-10 border-[3px]',
};

function Spinner({ size = 'md', className = '' }) {
  return (
    <span
      role="status"
      className={`inline-block animate-spin rounded-full border-current border-t-transparent text-primary ${sizeMap[size]} ${className}`}
    >
      <span className="sr-only">Loading...</span>
    </span>
  );
}

Spinner.propTypes = {
  size: PropTypes.oneOf(['sm', 'md', 'lg']),
  className: PropTypes.string,
};

export default Spinner;
