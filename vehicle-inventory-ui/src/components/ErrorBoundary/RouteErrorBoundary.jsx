import { useRouteError, useNavigate } from 'react-router-dom';
import { AlertTriangle, ArrowLeft } from 'lucide-react';
import Button from '../Button/Button';

function RouteErrorBoundary() {
  const error = useRouteError();
  const navigate = useNavigate();

  const message = error?.message || error?.statusText || 'An unexpected error occurred.';

  return (
    <div className="min-h-[400px] flex flex-col items-center justify-center p-8 text-center">
      <div className="flex items-center justify-center w-16 h-16 rounded-2xl bg-red-50 dark:bg-red-950/30 mb-4">
        <AlertTriangle size={32} className="text-danger" />
      </div>
      <h2 className="text-xl font-semibold text-text-primary mb-2">Something went wrong</h2>
      <p className="text-sm text-text-secondary mb-6 max-w-md">{message}</p>
      <div className="flex gap-3">
        <Button onClick={() => navigate(-1)} variant="secondary" icon={<ArrowLeft size={16} />}>
          Go Back
        </Button>
        <Button onClick={() => navigate('/vehicles')} variant="primary">
          Back to Inventory
        </Button>
      </div>
    </div>
  );
}

export default RouteErrorBoundary;
