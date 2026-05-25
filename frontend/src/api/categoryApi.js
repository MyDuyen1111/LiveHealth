import { get } from './apiClient';

export const categoryApi = {
  getAll: (pageNum = 1, pageSize = 50) =>
    get('/categories', { pageNum, pageSize }),
  getById: (id) => get(`/categories/${id}`),
};
