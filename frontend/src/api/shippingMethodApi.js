import { get } from './apiClient';

export const shippingMethodApi = {
  getAll: (pageNum = 1, pageSize = 50) =>
    get('/shipping-methods', { pageNum, pageSize }),
};
