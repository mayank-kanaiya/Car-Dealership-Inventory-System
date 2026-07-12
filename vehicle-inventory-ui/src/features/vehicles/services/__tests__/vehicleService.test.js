import { describe, it, expect } from 'vitest';
import vehicleService from '../vehicleService';

describe('vehicleService', () => {
  describe('getAll', () => {
    it('returns paginated vehicle list', async () => {
      const result = await vehicleService.getAll({ page: 0, size: 20 });
      expect(result.content).toBeDefined();
      expect(Array.isArray(result.content)).toBe(true);
      expect(result.page).toBe(0);
      expect(result.totalElements).toBeGreaterThanOrEqual(0);
    });
  });

  describe('search', () => {
    it('returns filtered results by make', async () => {
      const result = await vehicleService.search({ make: 'Toyota' });
      expect(result.content).toBeDefined();
      result.content.forEach((v) => {
        expect(v.make.toLowerCase()).toContain('toyota');
      });
    });

    it('returns filtered results by category', async () => {
      const result = await vehicleService.search({ category: 'SEDAN' });
      expect(result.content).toBeDefined();
      result.content.forEach((v) => {
        expect(v.category).toBe('SEDAN');
      });
    });

    it('returns all vehicles when no filters applied', async () => {
      const result = await vehicleService.search({});
      expect(result.content).toBeDefined();
      expect(result.content.length).toBeGreaterThan(0);
    });
  });

  describe('getById', () => {
    it('returns a vehicle by id', async () => {
      const list = await vehicleService.getAll({ size: 1 });
      const vehicle = list.content[0];
      const result = await vehicleService.getById(vehicle.id);
      expect(result.id).toBe(vehicle.id);
      expect(result.make).toBeDefined();
      expect(result.model).toBeDefined();
    });

    it('throws for non-existent id', async () => {
      await expect(vehicleService.getById('non-existent-id')).rejects.toThrow();
    });
  });
});
