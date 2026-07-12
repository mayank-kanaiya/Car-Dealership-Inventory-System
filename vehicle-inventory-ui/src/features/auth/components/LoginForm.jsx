import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useAuth } from '../hooks/useAuth';
import Input from '../../../components/Input/Input';
import Button from '../../../components/Button/Button';

const loginSchema = yup.object({
  email: yup.string().email('Please enter a valid email').required('Email is required'),
  password: yup.string().required('Password is required'),
});

function LoginForm() {
  const { login, isLoading } = useAuth();
  const [serverError, setServerError] = useState('');

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: yupResolver(loginSchema),
  });

  const onSubmit = async (data) => {
    setServerError('');
    try {
      await login(data);
    } catch (err) {
      setServerError(err.message || 'Login failed. Please try again.');
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4" noValidate>
      {serverError && (
        <div className="p-3 rounded-lg bg-red-50 border border-red-200 text-sm text-danger" role="alert">
          {serverError}
        </div>
      )}
      <Input
        label="Email"
        type="email"
        placeholder="john@example.com"
        error={errors.email?.message}
        {...register('email')}
      />
      <Input
        label="Password"
        type="password"
        placeholder="Enter your password"
        error={errors.password?.message}
        {...register('password')}
      />
      <Button type="submit" fullWidth isLoading={isSubmitting || isLoading}>
        Sign In
      </Button>
    </form>
  );
}

export default LoginForm;
