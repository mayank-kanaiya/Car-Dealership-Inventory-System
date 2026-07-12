import { Outlet, Link, useNavigate } from 'react-router-dom';
import { Moon, Sun } from 'lucide-react';
import { useAuth } from '../features/auth/hooks/useAuth';
import { useTheme } from '../context/ThemeContext';
import Button from '../components/Button/Button';

function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const { isDark, toggleTheme } = useTheme();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-surface border-b border-border">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <Link to="/vehicles" className="text-xl font-bold text-primary">
            Vehicle Inventory
          </Link>
          <div className="flex items-center gap-4">
            <Link
              to="/vehicles"
              className="text-sm text-text-secondary hover:text-text-primary transition-colors"
            >
              Inventory
            </Link>
            {isAdmin && (
              <Link
                to="/admin"
                className="text-sm text-text-secondary hover:text-text-primary transition-colors"
              >
                Admin
              </Link>
            )}
            <button
              onClick={toggleTheme}
              className="p-2 rounded-lg text-text-secondary hover:text-text-primary hover:bg-surface-hover transition-colors cursor-pointer"
              aria-label={isDark ? 'Switch to light mode' : 'Switch to dark mode'}
            >
              {isDark ? <Sun size={18} /> : <Moon size={18} />}
            </button>
            <span className="text-sm text-text-muted hidden sm:inline">{user?.fullName}</span>
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
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Navbar />
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>
    </div>
  );
}

export default MainLayout;
