import { api } from './client';

export const auditLogsApi = {
  getAll: (params = {}) => {
    const query = new URLSearchParams();
    if (params.baseId) query.append('baseId', params.baseId);
    if (params.action) query.append('action', params.action);
    if (params.entityType) query.append('entityType', params.entityType);
    if (params.startDateTime) query.append('startDateTime', params.startDateTime);
    if (params.endDateTime) query.append('endDateTime', params.endDateTime);

    const queryString = query.toString();
    return api.get(`/api/audit-logs${queryString ? `?${queryString}` : ''}`);
  },
};
