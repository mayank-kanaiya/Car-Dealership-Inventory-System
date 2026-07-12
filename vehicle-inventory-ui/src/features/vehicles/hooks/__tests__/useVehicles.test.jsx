import { renderHook, waitFor } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import PropTypes from 'prop-types';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useVehicleList, useVehicleDetail, VEHICLE_KEYS } from '../useVehicles';

function createWrapper() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  function Wrapper({ children }) {
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
  }
  Wrapper.propTypes = { children: PropTypes.node.isRequired };
  return Wrapper;
}

describe('useVehicles hooks', () => {
  describe('useVehicleList', () => {
    it('fetches vehicle list', async () => {
      const wrapper = createWrapper();
      const { result } = renderHook(() => useVehicleList(), { wrapper });
      await waitFor(() => expect(result.current.isSuccess).toBe(true));
      expect(result.current.data.content).toBeDefined();
      expect(result.current.data.content.length).toBeGreaterThan(0);
    });

    it('fetches filtered results', async () => {
      const wrapper = createWrapper();
      const { result } = renderHook(() => useVehicleList({ make: 'Toyota' }), { wrapper });
      await waitFor(() => expect(result.current.isSuccess).toBe(true));
      result.current.data.content.forEach((v) => {
        expect(v.make.toLowerCase()).toContain('toyota');
      });
    });
  });

  describe('useVehicleDetail', () => {
    it('fetches a vehicle by id', async () => {
      const wrapper = createWrapper();
      const { result } = renderHook(() => useVehicleList(), { wrapper });
      await waitFor(() => expect(result.current.isSuccess).toBe(true));
      const vehicleId = result.current.data.content[0].id;

      const { result: detailResult } = renderHook(() => useVehicleDetail(vehicleId), { wrapper });
      await waitFor(() => expect(detailResult.current.isSuccess).toBe(true));
      expect(detailResult.current.data.id).toBe(vehicleId);
    });

    it('does not fetch when id is null', async () => {
      const wrapper = createWrapper();
      const { result } = renderHook(() => useVehicleDetail(null), { wrapper });
      expect(result.current.fetchStatus).toBe('idle');
    });
  });

  describe('VEHICLE_KEYS', () => {
    it('generates correct cache keys', () => {
      expect(VEHICLE_KEYS.all).toEqual(['vehicles']);
      expect(VEHICLE_KEYS.lists()).toEqual(['vehicles', 'list']);
      expect(VEHICLE_KEYS.list({ make: 'Toyota' })).toEqual([
        'vehicles',
        'list',
        { make: 'Toyota' },
      ]);
      expect(VEHICLE_KEYS.detail('123')).toEqual(['vehicles', 'detail', '123']);
    });
  });
});
