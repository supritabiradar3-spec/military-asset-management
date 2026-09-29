import { api } from './client';

export const expendituresApi = {
  create: (data) => api.post('/api/expenditures', data),
  getById: (id) => api.get(`/api/expenditures/${id}`),
  getAll: (params = {}) => {
    const query = new URLSearchParams();
    if (params.baseId) query.append('baseId', params.baseId);
    if (params.equipmentTypeId) query.append('equipmentTypeId', params.equipmentTypeId);
    if (params.startDate) query.append('startDate', params.startDate);
    if (params.endDate) query.append('endDate', params.endDate);

    const queryString = query.toString();
    return api.get(`/api/expenditures${queryString ? `?${queryString}` : ''}`);
  },
};
