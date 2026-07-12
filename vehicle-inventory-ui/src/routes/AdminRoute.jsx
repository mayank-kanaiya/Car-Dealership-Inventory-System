import { Navigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import { useAuth } from '../features/auth/hooks/useAuth';
import Spinner from '../components/Spinner/Spinner';

function AdminRoute({ children }) {
  const { isAuthenticated, isAdmin, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Spinner size="lg" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!isAdmin) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
}

AdminRoute.propTypes = {
  children: PropTypes.node.isRequired,
};

export default AdminRoute;
