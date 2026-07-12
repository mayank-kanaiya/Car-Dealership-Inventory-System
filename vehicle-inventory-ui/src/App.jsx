import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './features/auth/context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import ErrorBoundary from './components/ErrorBoundary/ErrorBoundary';
import ProtectedRoute from './routes/ProtectedRoute';
import AdminRoute from './routes/AdminRoute';
import GuestRoute from './routes/GuestRoute';
import MainLayout from './layouts/MainLayout';
import AuthLayout from './layouts/AuthLayout';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import VehicleListPage from './pages/VehicleListPage';
import VehicleDetailPage from './pages/VehicleDetailPage';
import { CreateVehiclePage, EditVehiclePage } from './pages/VehicleFormPages';
import AdminDashboardPage from './pages/AdminDashboardPage';
import NotFoundPage from './pages/NotFoundPage';

function App() {
  return (
    <BrowserRouter>
      <ThemeProvider>
        <AuthProvider>
          <ErrorBoundary>
            <Toaster position="top-right" />
            <Routes>
              <Route element={<GuestRoute />}>
                <Route element={<AuthLayout />}>
                  <Route path="/login" element={<LoginPage />} />
                  <Route path="/register" element={<RegisterPage />} />
                </Route>
              </Route>

              <Route element={<ProtectedRoute />}>
                <Route element={<MainLayout />}>
                  <Route path="/vehicles" element={<VehicleListPage />} />
                  <Route path="/vehicles/new" element={<CreateVehiclePage />} />
                  <Route path="/vehicles/:id" element={<VehicleDetailPage />} />
                  <Route path="/vehicles/:id/edit" element={<EditVehiclePage />} />
                  <Route element={<AdminRoute />}>
                    <Route path="/admin" element={<AdminDashboardPage />} />
                  </Route>
                </Route>
              </Route>

              <Route path="/404" element={<NotFoundPage />} />
              <Route path="*" element={<Navigate to="/404" replace />} />
            </Routes>
          </ErrorBoundary>
        </AuthProvider>
      </ThemeProvider>
    </BrowserRouter>
  );
}

export default App;
