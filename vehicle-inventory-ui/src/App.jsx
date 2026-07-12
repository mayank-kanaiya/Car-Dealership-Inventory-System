import { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './features/auth/context/AuthContext';
import { ThemeProvider, useTheme } from './context/ThemeContext';
import ErrorBoundary from './components/ErrorBoundary/ErrorBoundary';
import RouteErrorBoundary from './components/ErrorBoundary/RouteErrorBoundary';
import ProtectedRoute from './routes/ProtectedRoute';
import AdminRoute from './routes/AdminRoute';
import GuestRoute from './routes/GuestRoute';
import MainLayout from './layouts/MainLayout';
import AuthLayout from './layouts/AuthLayout';
import Spinner from './components/Spinner/Spinner';

const LoginPage = lazy(() => import('./pages/LoginPage'));
const RegisterPage = lazy(() => import('./pages/RegisterPage'));
const VehicleListPage = lazy(() => import('./pages/VehicleListPage'));
const VehicleDetailPage = lazy(() => import('./pages/VehicleDetailPage'));
const VehicleFormPage = lazy(() => import('./pages/VehicleFormPages'));
const AdminDashboardPage = lazy(() => import('./pages/AdminDashboardPage'));
const NotFoundPage = lazy(() => import('./pages/NotFoundPage'));

const queryClient = new QueryClient();

function PageSpinner() {
  return (
    <div className="flex items-center justify-center min-h-[50vh]">
      <Spinner size="lg" />
    </div>
  );
}

function ThemedToaster() {
  const { isDark } = useTheme();
  return (
    <Toaster
      position="top-right"
      toastOptions={{
        duration: 4000,
        style: {
          background: isDark ? '#1e293b' : '#ffffff',
          color: isDark ? '#e2e8f0' : '#1e293b',
          border: `1px solid ${isDark ? '#334155' : '#e2e8f0'}`,
          borderRadius: '12px',
          padding: '12px 16px',
          fontSize: '14px',
          boxShadow: isDark
            ? '0 10px 15px -3px rgba(0, 0, 0, 0.3)'
            : '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
        },
        success: {
          iconTheme: {
            primary: '#22c55e',
            secondary: isDark ? '#1e293b' : '#ffffff',
          },
        },
        error: {
          iconTheme: {
            primary: '#ef4444',
            secondary: isDark ? '#1e293b' : '#ffffff',
          },
        },
      }}
    />
  );
}

function App() {
  return (
    <BrowserRouter>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider>
          <AuthProvider>
            <ErrorBoundary>
              <ThemedToaster />
              <Suspense fallback={<PageSpinner />}>
                <Routes>
                  <Route element={<GuestRoute />}>
                    <Route element={<AuthLayout />}>
                      <Route path="/login" element={<LoginPage />} />
                      <Route path="/register" element={<RegisterPage />} />
                    </Route>
                  </Route>

                  <Route element={<ProtectedRoute />}>
                    <Route element={<MainLayout />}>
                      <Route
                        path="/vehicles"
                        element={
                          <RouteErrorBoundary>
                            <VehicleListPage />
                          </RouteErrorBoundary>
                        }
                      />
                      <Route
                        path="/vehicles/new"
                        element={
                          <RouteErrorBoundary>
                            <VehicleFormPage />
                          </RouteErrorBoundary>
                        }
                      />
                      <Route
                        path="/vehicles/:id"
                        element={
                          <RouteErrorBoundary>
                            <VehicleDetailPage />
                          </RouteErrorBoundary>
                        }
                      />
                      <Route
                        path="/vehicles/:id/edit"
                        element={
                          <RouteErrorBoundary>
                            <VehicleFormPage />
                          </RouteErrorBoundary>
                        }
                      />
                      <Route element={<AdminRoute />}>
                        <Route
                          path="/admin"
                          element={
                            <RouteErrorBoundary>
                              <AdminDashboardPage />
                            </RouteErrorBoundary>
                          }
                        />
                      </Route>
                    </Route>
                  </Route>

                  <Route path="/404" element={<NotFoundPage />} />
                  <Route path="*" element={<Navigate to="/404" replace />} />
                </Routes>
              </Suspense>
            </ErrorBoundary>
          </AuthProvider>
        </ThemeProvider>
      </QueryClientProvider>
    </BrowserRouter>
  );
}

export default App;
