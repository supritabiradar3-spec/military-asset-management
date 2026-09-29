import { api } from './client';

export const assetsApi = {
  getAll: (params = {}) => {
    const query = new URLSearchParams();
    if (params.baseId) query.append('baseId', params.baseId);
    if (params.equipmentTypeId) query.append('equipmentTypeId', params.equipmentTypeId);

    const queryString = query.toString();
    return api.get(`/api/assets${queryString ? `?${queryString}` : ''}`);
  },
  getById: (id) => api.get(`/api/assets/${id}`),
  create: (data) => api.post('/api/assets', data),
};
