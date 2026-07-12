import { Navigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import { useAuth } from '../features/auth/hooks/useAuth';
import Spinner from '../components/Spinner/Spinner';

function GuestRoute({ children }) {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Spinner size="lg" />
      </div>
    );
  }

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}

GuestRoute.propTypes = {
  children: PropTypes.node.isRequired,
};

export default GuestRoute;
