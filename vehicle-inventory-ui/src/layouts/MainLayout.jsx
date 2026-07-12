import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../features/auth/hooks/useAuth';
import Button from '../components/Button/Button';

function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-surface border-b border-border">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <Link to="/dashboard" className="text-xl font-bold text-primary">
            Vehicle Inventory
          </Link>
          <div className="flex items-center gap-4">
            <Link to="/dashboard" className="text-sm text-text-secondary hover:text-text-primary transition-colors">
              Dashboard
            </Link>
            {isAdmin && (
              <Link to="/admin" className="text-sm text-text-secondary hover:text-text-primary transition-colors">
                Admin
              </Link>
            )}
            <span className="text-sm text-text-muted">{user?.fullName}</span>
            <Button variant="ghost" size="sm" onClick={handleLogout}>
              Logout
            </Button>
          </div>
        </div>
      </div>
    </nav>
  );
}

function MainLayout() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>
    </div>
  );
}

export default MainLayout;
