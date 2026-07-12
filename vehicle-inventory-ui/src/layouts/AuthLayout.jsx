import { Outlet } from 'react-router-dom';

function AuthLayout() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-primary">Vehicle Inventory</h1>
          <p className="text-sm text-text-secondary mt-1">Manage your dealership inventory</p>
        </div>
        <div className="bg-surface rounded-xl shadow-sm border border-border p-8">
          <Outlet />
        </div>
      </div>
    </div>
  );
}

export default AuthLayout;
