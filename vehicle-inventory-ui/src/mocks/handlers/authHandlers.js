import { http, HttpResponse } from 'msw';
import { mockAuthResponse } from '../data/mockData';

export const authHandlers = [
  http.post('*/api/v1/auth/login', async ({ request }) => {
    const body = await request.json();

    if (!body.email || !body.password) {
      return HttpResponse.json(
        {
          timestamp: new Date().toISOString(),
          status: 400,
          error: 'Validation Failed',
          message: 'One or more fields are invalid',
          path: '/api/v1/auth/login',
          errorCode: 'VALIDATION_ERROR',
          details: [{ field: 'email', message: 'Email is required', rejectedValue: null }],
        },
        { status: 400 }
      );
    }

    if (body.password === 'wrongpassword') {
      return HttpResponse.json(
        {
          timestamp: new Date().toISOString(),
          status: 401,
          error: 'Unauthorized',
          message: 'Invalid email or password',
          path: '/api/v1/auth/login',
          errorCode: 'INVALID_CREDENTIALS',
          details: null,
        },
        { status: 401 }
      );
    }

    return HttpResponse.json({
      ...mockAuthResponse,
      message: 'Login successful',
    });
  }),

  http.post('*/api/v1/auth/register', async ({ request }) => {
    const body = await request.json();

    if (body.email === 'existing@example.com') {
      return HttpResponse.json(
        {
          timestamp: new Date().toISOString(),
          status: 409,
          error: 'Conflict',
          message: 'An account with this email already exists',
          path: '/api/v1/auth/register',
          errorCode: 'USER_ALREADY_EXISTS',
          details: null,
        },
        { status: 409 }
      );
    }

    return HttpResponse.json(
      {
        message: 'User registered successfully',
        token: mockAuthResponse.token,
      },
      { status: 201 }
    );
  }),
];
