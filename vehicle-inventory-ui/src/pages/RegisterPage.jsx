import { Link } from 'react-router-dom';
import { UserPlus } from 'lucide-react';
import RegisterForm from '../features/auth/components/RegisterForm';

function RegisterPage() {
  return (
    <>
      <div className="text-center mb-6">
        <div className="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-primary/10 mb-3">
          <UserPlus size={22} className="text-primary" />
        </div>
        <h2 className="text-xl font-bold text-text-primary">Create an account</h2>
        <p className="text-sm text-text-secondary mt-1">Join us to manage your inventory</p>
      </div>
      <RegisterForm />
      <p className="text-center text-sm text-text-secondary mt-6">
        Already have an account?{' '}
        <Link
          to="/login"
          className="text-primary hover:text-primary-hover font-semibold transition-colors"
        >
          Sign in
        </Link>
      </p>
    </>
  );
}

export default RegisterPage;
