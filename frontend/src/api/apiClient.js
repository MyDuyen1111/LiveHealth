const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

function getToken() {
  return localStorage.getItem('accessToken');
}

function getRefreshToken() {
  return localStorage.getItem('refreshToken');
}

async function refreshAccessToken() {
  const refreshToken = getRefreshToken();
  if (!refreshToken) throw new Error('No refresh token');

  const res = await fetch(`${BASE_URL}/auth/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken }),
  });

  if (!res.ok) {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    throw new Error('Token refresh failed');
  }

  const json = await res.json();
  const data = json.data || json.message;
  localStorage.setItem('accessToken', data.accessToken);
  localStorage.setItem('refreshToken', data.refreshToken);
  return data.accessToken;
}

async function request(method, path, { body, params, isFormData } = {}) {
  let url = `${BASE_URL}${path}`;

  if (params) {
    const qs = new URLSearchParams();
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== null) qs.append(k, v);
    });
    const qsStr = qs.toString();
    if (qsStr) url += `?${qsStr}`;
  }

  const headers = {};
  const token = getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  let fetchBody;
  if (isFormData) {
    fetchBody = body; // FormData – browser sets Content-Type with boundary
  } else if (body) {
    headers['Content-Type'] = 'application/json';
    fetchBody = JSON.stringify(body);
  }

  let res = await fetch(url, { method, headers, body: fetchBody });

  // Auto-refresh on 401
  if (res.status === 401 && getRefreshToken()) {
    try {
      const newToken = await refreshAccessToken();
      headers['Authorization'] = `Bearer ${newToken}`;
      res = await fetch(url, { method, headers, body: fetchBody });
    } catch {
      // Refresh failed – let original 401 response through
    }
  }

  const json = await res.json().catch(() => ({}));

  if (!res.ok) {
    const errorPayload = json.message ?? json.data;
    const errMsg =
      typeof errorPayload === 'string'
        ? errorPayload
        : errorPayload && typeof errorPayload === 'object'
        ? Object.values(errorPayload).filter(Boolean).join(', ')
        : `Request failed (${res.status})`;
    const error = new Error(errMsg);
    error.status = res.status;
    error.body = json;
    throw error;
  }

  return json.data ?? json.message ?? json;
}

export const get = (path, params) => request('GET', path, { params });
export const post = (path, body) => request('POST', path, { body });
export const put = (path, body) => request('PUT', path, { body });
export const del = (path) => request('DELETE', path);
export const postForm = (path, formData) =>
  request('POST', path, { body: formData, isFormData: true });

export default {
  get,
  post,
  put,
  del,
  postForm,
};
