import { get } from './apiClient';

export const tagApi = {
  getAll: (pageNum = 1, pageSize = 50) =>
    get('/tags', { pageNum, pageSize }),
  getById: (id) => get(`/tags/${id}`),
};
