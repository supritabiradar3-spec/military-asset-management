import { api } from './client';

export const purchasesApi = {
  create: (data) => api.post('/api/purchases', data),
  getById: (id) => api.get(`/api/purchases/${id}`),
  getAll: (params = {}) => {
    const query = new URLSearchParams();
    if (params.baseId) query.append('baseId', params.baseId);
    if (params.equipmentTypeId) query.append('equipmentTypeId', params.equipmentTypeId);
    if (params.startDate) query.append('startDate', params.startDate);
    if (params.endDate) query.append('endDate', params.endDate);

    const queryString = query.toString();
    return api.get(`/api/purchases${queryString ? `?${queryString}` : ''}`);
  },
};
