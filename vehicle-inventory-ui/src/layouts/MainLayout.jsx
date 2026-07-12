import { useState } from 'react';
import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import { Moon, Sun, Menu, X, Car, LayoutDashboard, Shield, LogOut } from 'lucide-react';
import { useAuth } from '../features/auth/hooks/useAuth';
import { useTheme } from '../context/ThemeContext';

function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const { isDark, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  const navLinkClass = (path) =>
    `flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-all duration-150 ${
      isActive(path)
        ? 'bg-primary/10 text-primary'
        : 'text-text-secondary hover:text-text-primary hover:bg-surface-hover'
    }`;

  return (
    <nav className="bg-surface/80 backdrop-blur-xl border-b border-border sticky top-0 z-40">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <Link to="/vehicles" className="flex items-center gap-2.5 group">
            <div className="flex items-center justify-center w-9 h-9 rounded-xl bg-primary text-white group-hover:bg-primary-hover transition-colors">
              <Car size={20} />
            </div>
            <span className="text-lg font-bold text-text-primary hidden sm:block">VehicleHub</span>
          </Link>

          <div className="hidden md:flex items-center gap-1">
            <Link to="/vehicles" className={navLinkClass('/vehicles')}>
              <LayoutDashboard size={16} />
              Inventory
            </Link>
            {isAdmin && (
              <Link to="/admin" className={navLinkClass('/admin')}>
                <Shield size={16} />
                Admin
              </Link>
            )}
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={toggleTheme}
              className="p-2 rounded-xl text-text-secondary hover:text-text-primary hover:bg-surface-hover transition-all duration-150 cursor-pointer"
              aria-label={isDark ? 'Switch to light mode' : 'Switch to dark mode'}
            >
              {isDark ? <Sun size={18} /> : <Moon size={18} />}
            </button>
            <div className="hidden sm:flex items-center gap-3 ml-2 pl-3 border-l border-border">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                <span className="text-sm font-semibold text-primary">
                  {user?.fullName?.charAt(0) || 'U'}
                </span>
              </div>
              <span className="text-sm text-text-secondary max-w-[120px] truncate">
                {user?.fullName}
              </span>
            </div>
            <button
              onClick={handleLogout}
              className="hidden md:flex items-center gap-1.5 px-3 py-1.5 text-sm text-text-secondary hover:text-danger rounded-lg hover:bg-red-50 dark:hover:bg-red-950/30 transition-all duration-150 cursor-pointer"
            >
              <LogOut size={15} />
              Logout
            </button>
            <button
              onClick={() => setMobileOpen(!mobileOpen)}
              className="md:hidden p-2 rounded-xl text-text-secondary hover:text-text-primary hover:bg-surface-hover transition-colors cursor-pointer"
              aria-label="Toggle menu"
            >
              {mobileOpen ? <X size={20} /> : <Menu size={20} />}
            </button>
          </div>
        </div>
      </div>

      {mobileOpen && (
        <div className="md:hidden border-t border-border bg-surface">
          <div className="px-4 py-3 space-y-1">
            <Link
              to="/vehicles"
              className={navLinkClass('/vehicles')}
              onClick={() => setMobileOpen(false)}
            >
              <LayoutDashboard size={16} />
              Inventory
            </Link>
            {isAdmin && (
              <Link
                to="/admin"
                className={navLinkClass('/admin')}
                onClick={() => setMobileOpen(false)}
              >
                <Shield size={16} />
                Admin
              </Link>
            )}
            <div className="border-t border-border mt-2 pt-2">
              <div className="flex items-center gap-3 px-3 py-2">
                <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                  <span className="text-sm font-semibold text-primary">
                    {user?.fullName?.charAt(0) || 'U'}
                  </span>
                </div>
                <span className="text-sm text-text-secondary">{user?.fullName}</span>
              </div>
              <button
                onClick={handleLogout}
                className="flex items-center gap-2 w-full px-3 py-2 text-sm text-danger hover:bg-red-50 dark:hover:bg-red-950/30 rounded-lg transition-colors cursor-pointer"
              >
                <LogOut size={15} />
                Logout
              </button>
            </div>
          </div>
        </div>
      )}
    </nav>
  );
}

function MainLayout() {
  return (
    <div className="min-h-screen">
      <Navbar />
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8">
        <Outlet />
      </main>
    </div>
  );
}

export default MainLayout;
