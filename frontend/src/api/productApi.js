import { get } from './apiClient';

export const productApi = {
  getAll: (pageNum = 1, pageSize = 12, filters = {}) => {
    const params = { pageNum, pageSize };
    if (filters.categoryId) params.categoryId = filters.categoryId;
    if (filters.brandId)    params.brandId    = filters.brandId;
    if (filters.keyword)    params.keyword    = filters.keyword;
    if (filters.sortBy)     params.sortBy     = filters.sortBy;
    if (filters.sortType)   params.sortType   = filters.sortType;
    return get('/products', params);
  },
  getById: (id) => get(`/products/${id}`),
};
