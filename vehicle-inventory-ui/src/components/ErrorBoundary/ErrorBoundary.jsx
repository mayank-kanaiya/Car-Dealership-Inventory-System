import { Component } from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { AlertTriangle, Home } from 'lucide-react';
import Button from '../Button/Button';

class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('ErrorBoundary caught an error:', error, errorInfo);
  }

  handleRetry = () => {
    this.setState({ hasError: false, error: null });
  };

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-[400px] flex flex-col items-center justify-center p-8 text-center">
          <div className="flex items-center justify-center w-16 h-16 rounded-2xl bg-red-50 dark:bg-red-950/30 mb-4">
            <AlertTriangle size={32} className="text-danger" />
          </div>
          <h2 className="text-xl font-semibold text-text-primary mb-2">Something went wrong</h2>
          <p className="text-sm text-text-secondary mb-6 max-w-md">
            {this.state.error?.message || 'An unexpected error occurred. Please try again.'}
          </p>
          <div className="flex gap-3">
            <Button onClick={this.handleRetry} variant="primary">
              Try Again
            </Button>
            <Link
              to="/vehicles"
              className="inline-flex items-center gap-2 font-medium rounded-xl transition-all duration-150 bg-transparent text-text-primary hover:bg-surface-hover border border-border px-4 py-2.5 text-sm"
            >
              <Home size={16} />
              Go Home
            </Link>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

ErrorBoundary.propTypes = {
  children: PropTypes.node.isRequired,
};

export default ErrorBoundary;
