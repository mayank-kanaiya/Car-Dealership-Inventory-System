import PropTypes from 'prop-types';

function Skeleton({ className = '' }) {
  return (
    <div
      data-testid="skeleton"
      className={`animate-pulse bg-gray-200 h-4 w-full rounded ${className}`}
    />
  );
}

Skeleton.propTypes = {
  className: PropTypes.string,
};

export default Skeleton;
