import { api } from './client';

export const transfersApi = {
  create: (data) => api.post('/api/transfers', data),
  getById: (id) => api.get(`/api/transfers/${id}`),
  getAll: (params = {}) => {
    const query = new URLSearchParams();
    if (params.baseId) query.append('baseId', params.baseId);
    if (params.fromBaseId) query.append('fromBaseId', params.fromBaseId);
    if (params.toBaseId) query.append('toBaseId', params.toBaseId);
    if (params.equipmentTypeId) query.append('equipmentTypeId', params.equipmentTypeId);
    if (params.startDate) query.append('startDate', params.startDate);
    if (params.endDate) query.append('endDate', params.endDate);

    const queryString = query.toString();
    return api.get(`/api/transfers${queryString ? `?${queryString}` : ''}`);
  },
};
