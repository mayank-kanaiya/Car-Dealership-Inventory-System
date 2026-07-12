import PropTypes from 'prop-types';
import { render } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import { ThemeProvider } from '../context/ThemeContext';

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
        <MemoryRouter initialEntries={[route]}>
          <ThemeProvider>{children}</ThemeProvider>
        </MemoryRouter>
      </QueryClientProvider>
    );
  }

  Wrapper.propTypes = { children: PropTypes.node.isRequired };

  return {
    ...render(ui, { wrapper: Wrapper, ...renderOptions }),
    queryClient,
  };
}
