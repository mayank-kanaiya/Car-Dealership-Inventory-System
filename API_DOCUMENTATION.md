# Car Dealership Inventory System — API Documentation

> **Audience:** Frontend Developers
> **Base URL:** `http://localhost:8080/api/v1`
> **Content-Type:** `application/json` (unless explicitly stated otherwise)
> **Authentication:** JWT Bearer token in `Authorization` header

---

## Table of Contents

1. [Authentication Overview](#authentication-overview)
2. [Error Response Format](#error-response-format)
3. [Endpoints](#endpoints)
   - [POST /auth/register](#post-apiv1authregister)
   - [POST /auth/login](#post-apiv1authlogin)
   - [POST /vehicles](#post-apiv1vehicles)
   - [GET /vehicles](#get-apiv1vehicles)
   - [GET /vehicles/search](#get-apiv1vehiclessearch)
   - [GET /vehicles/{id}](#get-apiv1vehiclesid)
   - [PUT /vehicles/{id}](#put-apiv1vehiclesid)
   - [DELETE /vehicles/{id}](#delete-apiv1vehiclesid)
   - [POST /vehicles/{id}/image](#post-apiv1vehiclesidimage)
   - [POST /vehicles/{id}/purchase](#post-apiv1vehiclesidpurchase)
   - [POST /vehicles/{id}/restock](#post-apiv1vehiclesidrestock)

---

## Authentication Overview

Most endpoints require a valid JWT token. The token is obtained by calling the login or register endpoints.

### How to authenticate

1. Register or login to receive a token.
2. Include the token in every subsequent request:

```
Authorization: Bearer <token>
```

3. The token has an expiration. When it expires, the API will return a `401` response — login again to get a new token.

### User Roles

| Role | Permissions |
|------|-------------|
| `USER` | Register, login, create vehicles, update vehicles, list/search vehicles, purchase vehicles |
| `ADMIN` | Everything `USER` can do, plus **delete vehicles** and **restock vehicles** |

When you register, your default role is `USER`. Admin users must be assigned separately.

---

## Error Response Format

Every error returned by the API follows the same JSON structure. This makes it easy to build a global error handler on the frontend.

### Standard Error Response

```json
{
  "timestamp": "2026-07-12T10:30:00.000",
  "status": 404,
  "error": "Not Found",
  "message": "Vehicle not found with id: 550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/vehicles/550e8400-e29b-41d4-a716-446655440000",
  "errorCode": "RESOURCE_NOT_FOUND",
  "details": null
}
```

| Field | Type | Description |
|-------|------|-------------|
| `timestamp` | `string` (ISO 8601) | Date and time when the error occurred |
| `status` | `number` | HTTP status code (e.g. `404`, `409`, `400`) |
| `error` | `string` | HTTP status reason phrase (e.g. `"Not Found"`, `"Conflict"`) |
| `message` | `string` | Human-readable description of what went wrong. **Show this to the user.** |
| `path` | `string` | The API path that was called |
| `errorCode` | `string` | Machine-readable error code (see table below). Useful for programmatic branching on the frontend |
| `details` | `array \| null` | Present only for validation errors (400). Contains per-field error details |

### Validation Error Response (HTTP 400)

Returned when request body fields fail validation rules:

```json
{
  "timestamp": "2026-07-12T10:30:00.000",
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields are invalid",
  "path": "/api/v1/vehicles",
  "errorCode": "VALIDATION_ERROR",
  "details": [
    {
      "field": "make",
      "message": "Make is required",
      "rejectedValue": null
    },
    {
      "field": "price",
      "message": "Price must be positive",
      "rejectedValue": -50
    }
  ]
}
```

| Field | Type | Description |
|-------|------|-------------|
| `details[].field` | `string` | Name of the field that failed validation |
| `details[].message` | `string` | Validation error message (show to user) |
| `details[].rejectedValue` | `any` | The value that was sent and rejected (can be `null` if field was missing) |

### Error Code Reference

Use these codes for programmatic error handling on the frontend:

| HTTP Status | errorCode | When It Happens |
|-------------|-----------|-----------------|
| `400` | `VALIDATION_ERROR` | One or more request body fields are invalid |
| `400` | `MISSING_REQUEST_PART` | A required multipart file is missing (image upload) |
| `400` | `MALFORMED_REQUEST` | Request body is not valid JSON |
| `400` | `INVALID_PARAMETER` | A path or query parameter has an invalid type (e.g. `"not-a-uuid"` for an `{id}` field) |
| `401` | *(none)* | No token provided or token is missing/expired |
| `401` | `EXPIRED_TOKEN` | JWT token has expired (thrown by the application) |
| `401` | `INVALID_CREDENTIALS` | Email or password is wrong during login |
| `403` | `ACCESS_DENIED` | Authenticated user lacks required role (e.g. non-ADMIN trying to delete) |
| `404` | `RESOURCE_NOT_FOUND` | The requested vehicle or user does not exist |
| `409` | `DUPLICATE_VEHICLE` | A vehicle with the same make, model, and category already exists |
| `409` | `USER_ALREADY_EXISTS` | An account with this email already exists |
| `409` | `INSUFFICIENT_STOCK` | Purchase quantity exceeds available stock |
| `409` | `OPTIMISTIC_LOCK_CONFLICT` | Another request modified this resource at the same time. Retry the request |
| `415` | `UNSUPPORTED_MEDIA_TYPE` | Content-Type is not `application/json` (for JSON endpoints) |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error. Contact backend team |

---

## Enum Values

### VehicleCategory

Use these exact string values in the `category` field:

```
SEDAN, SUV, HATCHBACK, PICKUP_TRUCK, ELECTRIC_SUV, SPORTS_CAR,
MINIVAN, CROSSOVER, OFF_ROAD, MOTORCYCLE
```

---

## Endpoints

---

### POST /api/v1/auth/register

Register a new user account. Returns a JWT token immediately — the user is logged in upon registration.

**Authentication:** Not required (public endpoint)

#### Request

```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "securepass123"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `fullName` | `string` | Yes | Cannot be blank |
| `email` | `string` | Yes | Cannot be blank, must be valid email format |
| `password` | `string` | Yes | Cannot be blank, minimum 8 characters |

#### Success Response — `201 Created`

```json
{
  "message": "User registered successfully",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

| Field | Type | Description |
|-------|------|-------------|
| `message` | `string` | Confirmation message |
| `token` | `string` | JWT token to use in `Authorization` header for subsequent requests |

#### Error Responses

| HTTP Status | errorCode | When |
|-------------|-----------|------|
| `400` | `VALIDATION_ERROR` | Missing or invalid fields (e.g. short password, invalid email) |
| `409` | `USER_ALREADY_EXISTS` | An account with this email already exists |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error |

---

### POST /api/v1/auth/login

Authenticate an existing user and receive a JWT token.

**Authentication:** Not required (public endpoint)

#### Request

```json
{
  "email": "john@example.com",
  "password": "securepass123"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `email` | `string` | Yes | Cannot be blank |
| `password` | `string` | Yes | Cannot be blank |

#### Success Response — `200 OK`

```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

| Field | Type | Description |
|-------|------|-------------|
| `message` | `string` | Confirmation message |
| `token` | `string` | JWT token to use in `Authorization` header for subsequent requests |

#### Error Responses

| HTTP Status | errorCode | When |
|-------------|-----------|------|
| `400` | `VALIDATION_ERROR` | Missing email or password |
| `401` | `INVALID_CREDENTIALS` | Wrong email or password |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error |

---

### POST /api/v1/vehicles

Create a new vehicle in the inventory. A default placeholder image is assigned automatically.

**Authentication:** Required (any role)

#### Request

```json
{
  "make": "Toyota",
  "model": "Camry",
  "category": "SEDAN",
  "price": 28500.00,
  "quantityInStock": 15
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `make` | `string` | Yes | Cannot be blank, max 80 characters |
| `model` | `string` | Yes | Cannot be blank, max 80 characters |
| `category` | `string` | Yes | Must be one of the [VehicleCategory](#vehiclecategory) enum values |
| `price` | `number` | Yes | Must be positive, up to 10 integer digits and 2 decimal places |
| `quantityInStock` | `number` | Yes | Cannot be negative (minimum `0`) |

#### Success Response — `201 Created`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "make": "Toyota",
  "model": "Camry",
  "category": "SEDAN",
  "price": 28500.00,
  "quantityInStock": 15,
  "imageUrl": "/images/default-vehicle.svg"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `id` | `string` (UUID) | Unique identifier assigned to the vehicle (auto-generated) |
| `make` | `string` | Vehicle manufacturer |
| `model` | `string` | Vehicle model name |
| `category` | `string` | Vehicle category (enum value) |
| `price` | `number` | Vehicle price |
| `quantityInStock` | `number` | Current stock quantity |
| `imageUrl` | `string` | URL of the vehicle image. Defaults to `/images/default-vehicle.svg` |

#### Error Responses

| HTTP Status | errorCode | When |
|-------------|-----------|------|
| `400` | `VALIDATION_ERROR` | Invalid or missing fields |
| `400` | `MALFORMED_REQUEST` | Request body is not valid JSON or `category` has an invalid enum value |
| `409` | `DUPLICATE_VEHICLE` | A vehicle with the same make, model, and category already exists |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error |

---

### GET /api/v1/vehicles

Retrieve a paginated list of all vehicles in the inventory.

**Authentication:** Required (any role)

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `page` | `number` | No | `0` | Page number (zero-based). `0` = first page |
| `size` | `number` | No | `20` | Number of items per page |
| `sortBy` | `string` | No | `"id"` | Field to sort by (any VehicleResponse field) |
| `direction` | `string` | No | `"asc"` | Sort direction: `"asc"` (ascending) or `"desc"` (descending) |

**Example:** `GET /api/v1/vehicles?page=0&size=10&sortBy=price&direction=desc`

#### Success Response — `200 OK`

```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "make": "Toyota",
      "model": "Camry",
      "category": "SEDAN",
      "price": 28500.00,
      "quantityInStock": 15,
      "imageUrl": "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/abc123.jpg"
    },
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "make": "Honda",
      "model": "Civic",
      "category": "HATCHBACK",
      "price": 22000.00,
      "quantityInStock": 8,
      "imageUrl": "/images/default-vehicle.svg"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2,
  "totalPages": 1,
  "last": true
}
```

| Field | Type | Description |
|-------|------|-------------|
| `content` | `array` | List of vehicle objects (see [VehicleResponse](#vehicleresponse-fields) below) |
| `page` | `number` | Current page number (zero-based) |
| `size` | `number` | Page size that was requested |
| `totalElements` | `number` | Total number of vehicles matching the query |
| `totalPages` | `number` | Total number of pages available |
| `last` | `boolean` | `true` if this is the last page, `false` otherwise |

#### VehicleResponse Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | `string` (UUID) | Unique vehicle identifier |
| `make` | `string` | Vehicle manufacturer |
| `model` | `string` | Vehicle model name |
| `category` | `string` | Vehicle category (enum value) |
| `price` | `number` | Vehicle price |
| `quantityInStock` | `number` | Current stock quantity |
| `imageUrl` | `string` | Image URL. Use this directly in an `<img>` tag |

#### Error Responses

| HTTP Status | errorCode | When |
|-------------|-----------|------|
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error |

---

### GET /api/v1/vehicles/search

Search for vehicles using optional filters. All parameters are optional — only non-null parameters are applied as filters. Returns a paginated result.

**Authentication:** Required (any role)

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `make` | `string` | No | *(none)* | Filter by vehicle make (partial match, case-insensitive) |
| `model` | `string` | No | *(none)* | Filter by vehicle model (partial match, case-insensitive) |
| `category` | `string` | No | *(none)* | Filter by exact category. Must be a valid [VehicleCategory](#vehiclecategory) value |
| `minPrice` | `number` | No | *(none)* | Minimum price (inclusive) |
| `maxPrice` | `number` | No | *(none)* | Maximum price (inclusive) |
| `page` | `number` | No | `0` | Page number (zero-based) |
| `size` | `number` | No | `20` | Number of items per page |

**Example:** `GET /api/v1/vehicles/search?make=toy&category=SEDAN&minPrice=20000&page=0&size=5`

#### Success Response — `200 OK`

```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "make": "Toyota",
      "model": "Camry",
      "category": "SEDAN",
      "price": 28500.00,
      "quantityInStock": 15,
      "imageUrl": "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/abc123.jpg"
    }
  ],
  "page": 0,
  "size": 5,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

Response structure is identical to [GET /vehicles](#get-apiv1vehicles) — same `PagedResponse` wrapper with `VehicleResponse` objects.

#### Error Responses

| HTTP Status | errorCode | When |
|-------------|-----------|------|
| `400` | `INVALID_PARAMETER` | `category` is not a valid enum value, or `minPrice`/`maxPrice` is not a valid number |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error |

---

### GET /api/v1/vehicles/{id}

Retrieve a single vehicle by its unique ID.

**Authentication:** Required (any role)

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | `string` (UUID) | The vehicle's unique identifier |

**Example:** `GET /api/v1/vehicles/550e8400-e29b-41d4-a716-446655440000`

#### Success Response — `200 OK`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "make": "Toyota",
  "model": "Camry",
  "category": "SEDAN",
  "price": 28500.00,
  "quantityInStock": 15,
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/abc123.jpg"
}
```

#### Error Responses

| HTTP Status | errorCode | When |
|-------------|-----------|------|
| `400` | `INVALID_PARAMETER` | `{id}` is not a valid UUID format (e.g. `"not-a-uuid"`) |
| `404` | `RESOURCE_NOT_FOUND` | No vehicle exists with this ID |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error |

---

### PUT /api/v1/vehicles/{id}

Fully update an existing vehicle. All fields in the request body are required (this is a full replacement, not a partial update).

**Authentication:** Required (any role)

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | `string` (UUID) | The vehicle's unique identifier |

#### Request Body

```json
{
  "make": "Toyota",
  "model": "Camry LE",
  "category": "SEDAN",
  "price": 27500.00,
  "quantityInStock": 20
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `make` | `string` | Yes | Cannot be blank, max 80 characters |
| `model` | `string` | Yes | Cannot be blank, max 80 characters |
| `category` | `string` | Yes | Must be one of the [VehicleCategory](#vehiclecategory) enum values |
| `price` | `number` | Yes | Must be positive, up to 10 integer digits and 2 decimal places |
| `quantityInStock` | `number` | Yes | Cannot be negative (minimum `0`) |

#### Success Response — `200 OK`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "make": "Toyota",
  "model": "Camry LE",
  "category": "SEDAN",
  "price": 27500.00,
  "quantityInStock": 20,
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/abc123.jpg"
}
```

#### Error Responses

| HTTP Status | errorCode | When |
|-------------|-----------|------|
| `400` | `VALIDATION_ERROR` | Invalid or missing fields |
| `400` | `MALFORMED_REQUEST` | Request body is not valid JSON or `category` has an invalid enum value |
| `404` | `RESOURCE_NOT_FOUND` | No vehicle exists with this ID |
| `409` | `DUPLICATE_VEHICLE` | The updated make+model+category combination matches another existing vehicle |
| `409` | `OPTIMISTIC_LOCK_CONFLICT` | Another request modified this vehicle at the same time. Retry the request |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error |

---

### DELETE /api/v1/vehicles/{id}

Delete a vehicle from the inventory.

**Authentication:** Required — **ADMIN role only**

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | `string` (UUID) | The vehicle's unique identifier |

**Example:** `DELETE /api/v1/vehicles/550e8400-e29b-41d4-a716-446655440000`

#### Success Response — `204 No Content`

No response body.

#### Error Responses

| HTTP Status | errorCode | When |
|-------------|-----------|------|
| `400` | `INVALID_PARAMETER` | `{id}` is not a valid UUID format |
| `403` | `ACCESS_DENIED` | Authenticated user is not an ADMIN |
| `404` | `RESOURCE_NOT_FOUND` | No vehicle exists with this ID |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error |

---

### POST /api/v1/vehicles/{id}/image

Upload an image for a vehicle. The image is stored on Cloudinary and the vehicle's `imageUrl` is updated. If the vehicle previously had a Cloudinary image, it is deleted and replaced.

**Content-Type:** `multipart/form-data` (not JSON)

**Authentication:** Required (any role)

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | `string` (UUID) | The vehicle's unique identifier |

#### Request Body (multipart/form-data)

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `file` | `binary` | Yes | Image file (e.g. JPEG, PNG, WebP) |

**Example (cURL):**
```bash
curl -X POST http://localhost:8080/api/v1/vehicles/550e8400-e29b-41d4-a716-446655440000/image \
  -H "Authorization: Bearer <token>" \
  -F "file=@/path/to/car-photo.jpg"
```

#### Success Response — `200 OK`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "make": "Toyota",
  "model": "Camry",
  "category": "SEDAN",
  "price": 28500.00,
  "quantityInStock": 15,
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/1720786200_car-photo.jpg"
}
```

The `imageUrl` now points to the Cloudinary-hosted image. Use this URL directly in an `<img>` tag.

#### Error Responses

| HTTP Status | errorCode | When |
|-------------|-----------|------|
| `400` | `MISSING_REQUEST_PART` | The `file` field is missing from the multipart request |
| `400` | `INVALID_PARAMETER` | `{id}` is not a valid UUID format |
| `404` | `RESOURCE_NOT_FOUND` | No vehicle exists with this ID |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error (e.g. Cloudinary upload failure) |

---

### POST /api/v1/vehicles/{id}/purchase

Purchase a quantity of a vehicle. Decreases the `quantityInStock` by the requested amount.

**Authentication:** Required (any role)

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | `string` (UUID) | The vehicle's unique identifier |

#### Request Body

```json
{
  "quantity": 2
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `quantity` | `number` | Yes | Must be at least `1` |

#### Success Response — `200 OK`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "make": "Toyota",
  "model": "Camry",
  "category": "SEDAN",
  "price": 28500.00,
  "quantityInStock": 13,
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/abc123.jpg"
}
```

The returned vehicle reflects the updated stock. If stock drops to 5 or below, a low-stock alert is logged on the backend.

#### Error Responses

| HTTP Status | errorCode | When |
|-------------|-----------|------|
| `400` | `VALIDATION_ERROR` | `quantity` is missing or less than 1 |
| `404` | `RESOURCE_NOT_FOUND` | No vehicle exists with this ID |
| `409` | `INSUFFICIENT_STOCK` | Requested quantity exceeds available stock (e.g. buying 10 when only 3 are in stock) |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error |

---

### POST /api/v1/vehicles/{id}/restock

Restock a vehicle by adding to its inventory quantity. Only accessible to ADMIN users.

**Authentication:** Required — **ADMIN role only**

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | `string` (UUID) | The vehicle's unique identifier |

#### Request Body

```json
{
  "quantity": 10
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `quantity` | `number` | Yes | Must be at least `1` |

#### Success Response — `200 OK`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "make": "Toyota",
  "model": "Camry",
  "category": "SEDAN",
  "price": 28500.00,
  "quantityInStock": 25,
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/abc123.jpg"
}
```

The returned vehicle reflects the updated stock.

#### Error Responses

| HTTP Status | errorCode | When |
|-------------|-----------|------|
| `400` | `VALIDATION_ERROR` | `quantity` is missing or less than 1 |
| `403` | `ACCESS_DENIED` | Authenticated user is not an ADMIN |
| `404` | `RESOURCE_NOT_FOUND` | No vehicle exists with this ID |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error |

---

## Common Frontend Notes

### Pagination Pattern

All list/search endpoints return results wrapped in `PagedResponse`. To implement pagination on the frontend:

```javascript
// Fetch page 0 (first page), 10 items per page
const response = await fetch('/api/v1/vehicles?page=0&size=10', {
  headers: { 'Authorization': `Bearer ${token}` }
});
const data = await response.json();

data.content   // Array of VehicleResponse objects
data.page      // Current page (0-indexed)
data.size      // Items per page
data.totalElements  // Total matching items
data.totalPages     // Total available pages
data.last      // true if no more pages

// Next page: page + 1 (if !data.last)
// Previous page: page - 1 (if data.page > 0)
```

### Handling Image URLs

- A newly created vehicle starts with `"imageUrl": "/images/default-vehicle.svg"` — a local placeholder.
- After uploading an image via the image endpoint, `imageUrl` becomes a Cloudinary URL (e.g. `"https://res.cloudinary.com/..."`).
- Use the `imageUrl` value directly in an `<img>` tag. It is always a complete, valid URL (relative or absolute).

```html
<img src={vehicle.imageUrl} alt={`${vehicle.make} ${vehicle.model}`} />
```

### Optimistic Locking (409 Conflict)

The vehicle entity uses optimistic locking (via a hidden `version` field). If two users edit the same vehicle simultaneously, one request will succeed and the other will get a `409 OPTIMISTIC_LOCK_CONFLICT` error. In this case, the frontend should **refetch the vehicle data and retry the update**.

### JWT Token Storage

- Store the token in memory (e.g. React state/context) or `localStorage`.
- Include it as `Authorization: Bearer <token>` on every protected request.
- On `401` response, redirect the user to the login page.

### Recommended Default Pagination Values

```javascript
const DEFAULT_PAGE = 0;
const DEFAULT_SIZE = 20;
const DEFAULT_SORT_BY = 'id';
const DEFAULT_DIRECTION = 'asc';
```
