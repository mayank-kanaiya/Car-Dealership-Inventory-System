# Vehicle Inventory System — Frontend

React frontend for the Car Dealership Inventory System.

## Tech Stack

- **React 18** + Vite 8
- **React Router v7** — Client-side routing
- **TanStack React Query v5** — Server state management
- **Axios** — HTTP client
- **React Hook Form + Yup** — Form handling and validation
- **Tailwind CSS v4** — Utility-first styling
- **Lucide React** — Icons
- **React Hot Toast** — Notifications
- **Vitest** + **React Testing Library** — Unit/integration tests
- **MSW v2** — API mocking in tests
- **ESLint 9** + **Prettier** + **Husky + lint-staged** — Code quality

## Prerequisites

- Node.js >= 18
- npm >= 9

## Getting Started

```bash
cd vehicle-inventory-ui
npm install
npm run dev
```

The app runs at `http://localhost:5173` by default.

## Available Scripts

| Script              | Description                          |
| ------------------- | ------------------------------------ |
| `npm run dev`       | Start Vite dev server                |
| `npm run build`     | Production build                     |
| `npm run preview`   | Preview production build             |
| `npm test`          | Run tests in watch mode              |
| `npm run test:run`  | Run tests once                       |
| `npm run test:coverage` | Run tests with coverage report   |
| `npm run lint`      | Lint with ESLint                      |
| `npm run lint:fix`  | Lint and auto-fix                     |
| `npm run format`    | Format with Prettier                  |
| `npm run format:check` | Check formatting                  |

## Project Structure

```
src/
├── components/          # Shared/reusable UI components
├── features/
│   ├── auth/            # Authentication (login, register, logout)
│   │   ├── components/  # Auth-specific UI components
│   │   ├── context/     # AuthContext provider
│   │   ├── hooks/       # useAuth, useLogin, useRegister
│   │   └── services/    # authAPI (login, register, getProfile, logout)
│   ├── vehicles/        # Vehicle CRUD, listing, search, inventory
│   │   ├── components/  # VehicleCard, VehicleList, VehicleFilters
│   │   ├── hooks/       # useVehicles, useVehicle, useCreateVehicle, etc.
│   │   └── services/    # vehiclesAPI (CRUD, search, stats)
│   └── admin/           # Admin-only management features
│       ├── components/  # UserList, etc.
│       └── hooks/       # useUsers, useDeleteUser
├── hooks/               # Global hooks (useDebounce, usePagination, useLocalStorage)
├── layouts/             # AppLayout (ProtectedRoute + AdminRoute wrappers)
├── pages/               # Route-level page components
├── routes/              # React Router route definitions
├── utils/               # API client, formatters, validators, image helpers, constants
├── mocks/               # MSW handlers and browser setup for dev/test
├── test-utils/          # Custom render wrapper (providers, router, query client)
├── config/              # Environment config helper
└── constants/           # App-wide constants (categories, error codes, etc.)
```

## Environment Variables

Copy `.env.example` to `.env.development` and configure:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_APP_NAME=Vehicle Inventory System
```

## Testing

Tests live alongside source files as `*.test.jsx` or in `__tests__/` directories.

```bash
npm test            # Watch mode
npm run test:run    # Single run
```

MSW intercepts API calls in tests — handlers are in `src/mocks/handlers/`.

## API

This frontend consumes the Spring Boot backend API documented in `API_DOCUMENTATION.md` at the repo root.
