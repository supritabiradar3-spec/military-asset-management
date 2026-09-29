import { api } from './client';

export const basesApi = {
  getAll: () => api.get('/api/bases'),
  getById: (id) => api.get(`/api/bases/${id}`),
  create: (data) => api.post('/api/bases', data),
};
