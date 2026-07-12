import { Link } from 'react-router-dom';
import { LogIn } from 'lucide-react';
import LoginForm from '../features/auth/components/LoginForm';

function LoginPage() {
  return (
    <>
      <div className="text-center mb-6">
        <div className="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-primary/10 mb-3">
          <LogIn size={22} className="text-primary" />
        </div>
        <h2 className="text-xl font-bold text-text-primary">Welcome back</h2>
        <p className="text-sm text-text-secondary mt-1">Sign in to your account</p>
      </div>
      <LoginForm />
      <p className="text-center text-sm text-text-secondary mt-6">
        Don&apos;t have an account?{' '}
        <Link
          to="/register"
          className="text-primary hover:text-primary-hover font-semibold transition-colors"
        >
          Create one
        </Link>
      </p>
    </>
  );
}

export default LoginPage;
