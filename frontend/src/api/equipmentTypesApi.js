import { api } from './client';

export const equipmentTypesApi = {
  getAll: () => api.get('/api/equipment-types'),
  getById: (id) => api.get(`/api/equipment-types/${id}`),
  create: (data) => api.post('/api/equipment-types', data),
};
