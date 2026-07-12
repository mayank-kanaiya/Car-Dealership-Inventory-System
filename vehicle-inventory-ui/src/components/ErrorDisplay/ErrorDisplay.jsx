import PropTypes from 'prop-types';
import { AlertTriangle } from 'lucide-react';
import Button from '../Button/Button';

function ErrorDisplay({ title = 'Error', message, onRetry }) {
  return (
    <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
      <div data-testid="error-icon" className="mb-4 text-danger">
        <AlertTriangle size={48} strokeWidth={1.5} />
      </div>
      <h3 className="text-lg font-semibold text-text-primary mb-1">{title}</h3>
      <p className="text-sm text-text-secondary max-w-sm mb-4">{message}</p>
      {onRetry && (
        <Button variant="primary" onClick={onRetry}>
          Try Again
        </Button>
      )}
    </div>
  );
}

ErrorDisplay.propTypes = {
  title: PropTypes.string,
  message: PropTypes.string.isRequired,
  onRetry: PropTypes.func,
};

export default ErrorDisplay;
