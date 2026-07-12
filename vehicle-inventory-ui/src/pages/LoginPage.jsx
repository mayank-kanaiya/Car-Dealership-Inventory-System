import { Link } from 'react-router-dom';
import LoginForm from '../features/auth/components/LoginForm';

function LoginPage() {
  return (
    <>
      <h2 className="text-xl font-semibold text-text-primary text-center mb-6">Sign In</h2>
      <LoginForm />
      <p className="text-center text-sm text-text-secondary mt-6">
        Don&apos;t have an account?{' '}
        <Link
          to="/register"
          className="text-primary hover:text-primary-hover font-medium transition-colors"
        >
          Create one
        </Link>
      </p>
    </>
  );
}

export default LoginPage;
