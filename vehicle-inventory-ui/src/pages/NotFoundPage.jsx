import { Link } from 'react-router-dom';

function NotFoundPage() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-8 text-center">
      <h1 className="text-6xl font-bold text-primary mb-4">404</h1>
      <p className="text-xl text-text-secondary mb-8">Page not found</p>
      <Link
        to="/vehicles"
        className="inline-flex items-center justify-center font-medium rounded-lg transition-colors duration-200 bg-primary text-white hover:bg-primary-hover focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary px-4 py-2 text-sm"
      >
        Back to Inventory
      </Link>
    </div>
  );
}

export default NotFoundPage;
