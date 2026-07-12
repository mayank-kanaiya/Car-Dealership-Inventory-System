import apiClient from '../../../services/apiClient';

const vehicleService = {
  async getAll({ page = 0, size = 20, sortBy = 'id', direction = 'asc' } = {}) {
    const response = await apiClient.get('/vehicles', {
      params: { page, size, sortBy, direction },
    });
    return response.data;
  },

  async search({ make, model, category, minPrice, maxPrice, page = 0, size = 20 } = {}) {
    const params = {};
    if (make) params.make = make;
    if (model) params.model = model;
    if (category) params.category = category;
    if (minPrice != null) params.minPrice = minPrice;
    if (maxPrice != null) params.maxPrice = maxPrice;
    params.page = page;
    params.size = size;

    const response = await apiClient.get('/vehicles/search', { params });
    return response.data;
  },

  async getById(id) {
    const response = await apiClient.get(`/vehicles/${id}`);
    return response.data;
  },

  async create(vehicleData) {
    const response = await apiClient.post('/vehicles', vehicleData);
    return response.data;
  },

  async update(id, vehicleData) {
    const response = await apiClient.put(`/vehicles/${id}`, vehicleData);
    return response.data;
  },

  async delete(id) {
    await apiClient.delete(`/vehicles/${id}`);
  },

  async purchase(id, quantity) {
    const response = await apiClient.post(`/vehicles/${id}/purchase`, { quantity });
    return response.data;
  },

  async restock(id, quantity) {
    const response = await apiClient.post(`/vehicles/${id}/restock`, { quantity });
    return response.data;
  },

  async uploadImage(id, file) {
    const formData = new FormData();
    formData.append('file', file);
    const response = await apiClient.post(`/vehicles/${id}/image`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },
};

export default vehicleService;
