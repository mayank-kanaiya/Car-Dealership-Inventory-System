import { Link } from 'react-router-dom';
import { Car, ArrowLeft } from 'lucide-react';

function NotFoundPage() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-8 text-center bg-gradient-to-br from-surface-secondary to-surface">
      <div className="flex items-center justify-center w-20 h-20 rounded-2xl bg-primary/10 mb-6">
        <Car size={40} className="text-primary" />
      </div>
      <h1 className="text-7xl font-extrabold text-primary mb-2">404</h1>
      <p className="text-xl font-semibold text-text-primary mb-2">Page not found</p>
      <p className="text-sm text-text-secondary mb-8 max-w-sm">
        The page you are looking for does not exist or has been moved.
      </p>
      <Link
        to="/vehicles"
        className="inline-flex items-center gap-2 font-medium rounded-xl transition-all duration-200 bg-primary text-white hover:bg-primary-hover focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary px-6 py-3 text-sm"
      >
        <ArrowLeft size={16} />
        Back to Inventory
      </Link>
    </div>
  );
}

export default NotFoundPage;
