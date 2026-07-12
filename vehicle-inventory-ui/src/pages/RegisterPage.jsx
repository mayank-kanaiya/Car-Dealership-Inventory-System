import { Link } from 'react-router-dom';
import RegisterForm from '../features/auth/components/RegisterForm';

function RegisterPage() {
  return (
    <>
      <h2 className="text-xl font-semibold text-text-primary text-center mb-6">Create Account</h2>
      <RegisterForm />
      <p className="text-center text-sm text-text-secondary mt-6">
        Already have an account?{' '}
        <Link to="/login" className="text-primary hover:text-primary-hover font-medium transition-colors">
          Sign in
        </Link>
      </p>
    </>
  );
}

export default RegisterPage;
