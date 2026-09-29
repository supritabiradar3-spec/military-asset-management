// Centralized API Client

const RAW_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';
const BASE_URL = RAW_BASE_URL.replace(/\/+$/, '');

export async function apiClient(endpoint, { body, method = 'GET', customHeaders = {}, ...customConfig } = {}) {
  const token = localStorage.getItem('token');

  const headers = {
    'Content-Type': 'application/json',
    ...customHeaders,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const config = {
    method,
    headers,
    ...customConfig,
  };

  if (body) {
    config.body = typeof body === 'string' ? body : JSON.stringify(body);
  }

  const url = endpoint.startsWith('http') ? endpoint : `${BASE_URL}${endpoint}`;

  let response;
  try {
    response = await fetch(url, config);
  } catch (err) {
    throw new Error('Network error: Unable to connect to server. Please check backend status.');
  }

  if (response.status === 401) {
    // Clear credentials and redirect to login
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    if (window.location.pathname !== '/login') {
      window.location.href = '/login?expired=true';
    }
    throw new Error('Session expired or invalid credentials. Please log in.');
  }

  if (response.status === 403) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || 'Access Denied: You do not have permission to perform this operation.');
  }

  if (response.status === 204) {
    return null;
  }

  let data;
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    data = await response.json();
  } else {
    data = await response.text();
  }

  if (!response.ok) {
    let errorMsg = 'An unexpected error occurred.';
    if (data && typeof data === 'object') {
      if (data.details && Array.isArray(data.details)) {
        errorMsg = data.details.join(', ');
      } else if (data.message) {
        errorMsg = data.message;
      } else if (data.error) {
        errorMsg = data.error;
      }
    } else if (typeof data === 'string' && data) {
      errorMsg = data;
    }
    throw new Error(errorMsg);
  }

  return data;
}

export const api = {
  get: (endpoint, config = {}) => apiClient(endpoint, { method: 'GET', ...config }),
  post: (endpoint, body, config = {}) => apiClient(endpoint, { method: 'POST', body, ...config }),
  put: (endpoint, body, config = {}) => apiClient(endpoint, { method: 'PUT', body, ...config }),
  delete: (endpoint, config = {}) => apiClient(endpoint, { method: 'DELETE', ...config }),
};
