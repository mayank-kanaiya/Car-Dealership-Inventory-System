export const mockVehicles = [
  {
    id: '550e8400-e29b-41d4-a716-446655440000',
    make: 'Toyota',
    model: 'Camry',
    category: 'SEDAN',
    price: 28500.0,
    quantityInStock: 15,
    imageUrl:
      'https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/toyota-camry.jpg',
  },
  {
    id: '660e8400-e29b-41d4-a716-446655440001',
    make: 'Honda',
    model: 'Civic',
    category: 'HATCHBACK',
    price: 22000.0,
    quantityInStock: 8,
    imageUrl: '/images/default-vehicle.svg',
  },
  {
    id: '770e8400-e29b-41d4-a716-446655440002',
    make: 'Ford',
    model: 'F-150',
    category: 'PICKUP_TRUCK',
    price: 45000.0,
    quantityInStock: 0,
    imageUrl: '/images/default-vehicle.svg',
  },
];

export const mockAuthResponse = {
  message: 'Login successful',
  token:
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwicm9sZSI6IlVTRVIifQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c',
};

export const mockUser = {
  fullName: 'John Doe',
  email: 'john@example.com',
  role: 'USER',
};
