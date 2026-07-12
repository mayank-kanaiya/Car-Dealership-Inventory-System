import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import vehicleService from '../services/vehicleService';

export const VEHICLE_KEYS = {
  all: ['vehicles'],
  lists: () => [...VEHICLE_KEYS.all, 'list'],
  list: (filters) => [...VEHICLE_KEYS.lists(), filters],
  details: () => [...VEHICLE_KEYS.all, 'detail'],
  detail: (id) => [...VEHICLE_KEYS.details(), id],
};

export function useVehicleList(filters = {}) {
  const { make, category, minPrice, maxPrice, page = 0, size = 20 } = filters;
  const hasFilters = make || category || minPrice != null || maxPrice != null;

  return useQuery({
    queryKey: hasFilters ? VEHICLE_KEYS.list(filters) : VEHICLE_KEYS.lists(),
    queryFn: () => {
      if (hasFilters) {
        return vehicleService.search({ make, category, minPrice, maxPrice, page, size });
      }
      return vehicleService.getAll({ page, size });
    },
  });
}

export function useVehicleDetail(id) {
  return useQuery({
    queryKey: VEHICLE_KEYS.detail(id),
    queryFn: () => vehicleService.getById(id),
    enabled: !!id,
  });
}

export function useCreateVehicle() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: vehicleService.create,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: VEHICLE_KEYS.all }),
  });
}

export function useUpdateVehicle() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }) => vehicleService.update(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: VEHICLE_KEYS.all });
      queryClient.invalidateQueries({ queryKey: VEHICLE_KEYS.detail(id) });
    },
  });
}

export function useDeleteVehicle() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: vehicleService.delete,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: VEHICLE_KEYS.all }),
  });
}

export function usePurchase() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, quantity }) => vehicleService.purchase(id, quantity),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: VEHICLE_KEYS.all });
      queryClient.invalidateQueries({ queryKey: VEHICLE_KEYS.detail(id) });
    },
  });
}

export function useRestock() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, quantity }) => vehicleService.restock(id, quantity),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: VEHICLE_KEYS.all });
      queryClient.invalidateQueries({ queryKey: VEHICLE_KEYS.detail(id) });
    },
  });
}
