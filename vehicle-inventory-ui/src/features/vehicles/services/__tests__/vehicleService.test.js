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
    });

    it('throws for non-existent id', async () => {
      await expect(vehicleService.getById('non-existent-id')).rejects.toThrow();
    });
  });

  describe('create', () => {
    it('creates a new vehicle', async () => {
      const newVehicle = {
        make: 'Tesla',
        model: 'Model 3',
        category: 'ELECTRIC_SUV',
        price: 42000,
        quantityInStock: 5,
      };
      const result = await vehicleService.create(newVehicle);
      expect(result.id).toBeDefined();
      expect(result.make).toBe('Tesla');
      expect(result.model).toBe('Model 3');
    });
  });

  describe('update', () => {
    it('updates an existing vehicle', async () => {
      const list = await vehicleService.getAll({ size: 1 });
      const vehicle = list.content[0];
      const updated = await vehicleService.update(vehicle.id, {
        ...vehicle,
        price: 35000,
      });
      expect(updated.price).toBe(35000);
    });
  });

  describe('delete', () => {
    it('deletes a vehicle', async () => {
      const created = await vehicleService.create({
        make: 'Delete Me',
        model: 'Temp',
        category: 'SEDAN',
        price: 10000,
        quantityInStock: 1,
      });
      await expect(vehicleService.delete(created.id)).resolves.not.toThrow();
    });
  });

  describe('purchase', () => {
    it('reduces stock after purchase', async () => {
      const list = await vehicleService.getAll({ size: 1 });
      const vehicle = list.content[0];
      if (vehicle.quantityInStock > 0) {
        const result = await vehicleService.purchase(vehicle.id, 1);
        expect(result.quantityInStock).toBe(vehicle.quantityInStock - 1);
      }
    });
  });

  describe('restock', () => {
    it('increases stock after restock', async () => {
      const list = await vehicleService.getAll({ size: 1 });
      const vehicle = list.content[0];
      const result = await vehicleService.restock(vehicle.id, 10);
      expect(result.quantityInStock).toBe(vehicle.quantityInStock + 10);
    });
  });

  describe('uploadImage', () => {
    it('uploads image for a vehicle', async () => {
      const list = await vehicleService.getAll({ size: 1 });
      const vehicle = list.content[0];
      const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
      const result = await vehicleService.uploadImage(vehicle.id, file);
      expect(result.imageUrl).toBeDefined();
    });
  });
});
