import PropTypes from 'prop-types';
import { render } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';

export function renderWithProviders(
  ui,
  {
    route = '/',
    queryClient = new QueryClient({
      defaultOptions: { queries: { retry: false } },
    }),
    ...renderOptions
  } = {}
) {
  function Wrapper({ children }) {
    return (
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={[route]}>{children}</MemoryRouter>
      </QueryClientProvider>
    );
  }

  Wrapper.propTypes = { children: PropTypes.node.isRequired };

  return {
    ...render(ui, { wrapper: Wrapper, ...renderOptions }),
    queryClient,
  };
}
