import { http, HttpResponse } from 'msw';
import { mockVehicles } from '../data/mockData';

let vehicles = [...mockVehicles];

export const vehicleHandlers = [
  http.get('*/api/v1/vehicles/search', ({ request }) => {
    const url = new URL(request.url);
    const make = url.searchParams.get('make');
    const category = url.searchParams.get('category');
    const minPrice = url.searchParams.get('minPrice');
    const maxPrice = url.searchParams.get('maxPrice');
    const page = parseInt(url.searchParams.get('page') || '0', 10);
    const size = parseInt(url.searchParams.get('size') || '20', 10);

    let results = [...vehicles];

    if (make) {
      results = results.filter((v) => v.make.toLowerCase().includes(make.toLowerCase()));
    }
    if (category) {
      results = results.filter((v) => v.category === category);
    }
    if (minPrice) {
      results = results.filter((v) => v.price >= parseFloat(minPrice));
    }
    if (maxPrice) {
      results = results.filter((v) => v.price <= parseFloat(maxPrice));
    }

    const start = page * size;
    const paginated = results.slice(start, start + size);

    return HttpResponse.json({
      content: paginated,
      page,
      size,
      totalElements: results.length,
      totalPages: Math.ceil(results.length / size),
      last: start + size >= results.length,
    });
  }),

  http.get('*/api/v1/vehicles', ({ request }) => {
    const url = new URL(request.url);
    const page = parseInt(url.searchParams.get('page') || '0', 10);
    const size = parseInt(url.searchParams.get('size') || '20', 10);
    const sortBy = url.searchParams.get('sortBy') || 'id';
    const direction = url.searchParams.get('direction') || 'asc';

    let sorted = [...vehicles];
    if (sortBy && sorted[0]?.[sortBy] !== undefined) {
      sorted.sort((a, b) => {
        if (direction === 'desc') return a[sortBy] > b[sortBy] ? -1 : 1;
        return a[sortBy] > b[sortBy] ? 1 : -1;
      });
    }

    const start = page * size;
    const paginated = sorted.slice(start, start + size);

    return HttpResponse.json({
      content: paginated,
      page,
      size,
      totalElements: sorted.length,
      totalPages: Math.ceil(sorted.length / size),
      last: start + size >= sorted.length,
    });
  }),

  http.get('*/api/v1/vehicles/:id', ({ params }) => {
    const vehicle = vehicles.find((v) => v.id === params.id);
    if (!vehicle) {
      return HttpResponse.json(
        {
          timestamp: new Date().toISOString(),
          status: 404,
          error: 'Not Found',
          message: `Vehicle not found with id: ${params.id}`,
          path: `/api/v1/vehicles/${params.id}`,
          errorCode: 'RESOURCE_NOT_FOUND',
          details: null,
        },
        { status: 404 }
      );
    }
    return HttpResponse.json(vehicle);
  }),

  http.post('*/api/v1/vehicles', async ({ request }) => {
    const body = await request.json();
    const newVehicle = {
      id: crypto.randomUUID(),
      make: body.make,
      model: body.model,
      category: body.category,
      price: body.price,
      quantityInStock: body.quantityInStock,
      imageUrl: '/images/default-vehicle.svg',
    };
    vehicles.push(newVehicle);
    return HttpResponse.json(newVehicle, { status: 201 });
  }),

  http.put('*/api/v1/vehicles/:id', async ({ params, request }) => {
    const body = await request.json();
    const index = vehicles.findIndex((v) => v.id === params.id);
    if (index === -1) {
      return HttpResponse.json(
        {
          timestamp: new Date().toISOString(),
          status: 404,
          error: 'Not Found',
          message: `Vehicle not found with id: ${params.id}`,
          path: `/api/v1/vehicles/${params.id}`,
          errorCode: 'RESOURCE_NOT_FOUND',
          details: null,
        },
        { status: 404 }
      );
    }
    vehicles[index] = { ...vehicles[index], ...body };
    return HttpResponse.json(vehicles[index]);
  }),

  http.delete('*/api/v1/vehicles/:id', ({ params }) => {
    const index = vehicles.findIndex((v) => v.id === params.id);
    if (index === -1) {
      return HttpResponse.json(
        {
          timestamp: new Date().toISOString(),
          status: 404,
          error: 'Not Found',
          message: `Vehicle not found with id: ${params.id}`,
          path: `/api/v1/vehicles/${params.id}`,
          errorCode: 'RESOURCE_NOT_FOUND',
          details: null,
        },
        { status: 404 }
      );
    }
    vehicles.splice(index, 1);
    return new HttpResponse(null, { status: 204 });
  }),

  http.post('*/api/v1/vehicles/:id/purchase', async ({ params, request }) => {
    const body = await request.json();
    const vehicle = vehicles.find((v) => v.id === params.id);

    if (!vehicle) {
      return HttpResponse.json(
        {
          timestamp: new Date().toISOString(),
          status: 404,
          error: 'Not Found',
          message: `Vehicle not found with id: ${params.id}`,
          path: `/api/v1/vehicles/${params.id}/purchase`,
          errorCode: 'RESOURCE_NOT_FOUND',
          details: null,
        },
        { status: 404 }
      );
    }

    if (vehicle.quantityInStock < body.quantity) {
      return HttpResponse.json(
        {
          timestamp: new Date().toISOString(),
          status: 409,
          error: 'Conflict',
          message: `Insufficient stock. Available: ${vehicle.quantityInStock}, requested: ${body.quantity}`,
          path: `/api/v1/vehicles/${params.id}/purchase`,
          errorCode: 'INSUFFICIENT_STOCK',
          details: null,
        },
        { status: 409 }
      );
    }

    vehicle.quantityInStock -= body.quantity;
    return HttpResponse.json(vehicle);
  }),

  http.post('*/api/v1/vehicles/:id/restock', async ({ params, request }) => {
    const body = await request.json();
    const vehicle = vehicles.find((v) => v.id === params.id);

    if (!vehicle) {
      return HttpResponse.json(
        {
          timestamp: new Date().toISOString(),
          status: 404,
          error: 'Not Found',
          message: `Vehicle not found with id: ${params.id}`,
          path: `/api/v1/vehicles/${params.id}/restock`,
          errorCode: 'RESOURCE_NOT_FOUND',
          details: null,
        },
        { status: 404 }
      );
    }

    vehicle.quantityInStock += body.quantity;
    return HttpResponse.json(vehicle);
  }),

  http.post('*/api/v1/vehicles/:id/image', async ({ params }) => {
    const vehicle = vehicles.find((v) => v.id === params.id);
    if (!vehicle) {
      return HttpResponse.json(
        {
          timestamp: new Date().toISOString(),
          status: 404,
          error: 'Not Found',
          message: `Vehicle not found with id: ${params.id}`,
          path: `/api/v1/vehicles/${params.id}/image`,
          errorCode: 'RESOURCE_NOT_FOUND',
          details: null,
        },
        { status: 404 }
      );
    }
    vehicle.imageUrl =
      'https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/uploaded-image.jpg';
    return HttpResponse.json(vehicle);
  }),
];
