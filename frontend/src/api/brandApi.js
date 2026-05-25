import { get } from './apiClient';

export const brandApi = {
  getAll: (pageNum = 1, pageSize = 50) =>
    get('/brands', { pageNum, pageSize }),
  getById: (id) => get(`/brands/${id}`),
};
