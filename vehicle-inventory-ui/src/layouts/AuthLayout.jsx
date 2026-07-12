import { Outlet } from 'react-router-dom';
import { Car } from 'lucide-react';

function AuthLayout() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary via-primary-hover to-indigo-900 px-4 relative overflow-hidden">
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-20 left-20 w-72 h-72 bg-white rounded-full blur-3xl" />
        <div className="absolute bottom-20 right-20 w-96 h-96 bg-white rounded-full blur-3xl" />
      </div>
      <div className="w-full max-w-md relative z-10">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-white/15 backdrop-blur-sm mb-4">
            <Car className="text-white" size={32} />
          </div>
          <h1 className="text-3xl font-bold text-white tracking-tight">VehicleHub</h1>
          <p className="text-sm text-white/70 mt-1">Manage your dealership inventory</p>
        </div>
        <div className="bg-white/95 backdrop-blur-xl rounded-2xl shadow-2xl border border-white/20 p-8">
          <Outlet />
        </div>
      </div>
    </div>
  );
}

export default AuthLayout;
