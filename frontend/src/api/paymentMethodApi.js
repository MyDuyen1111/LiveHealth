import { get } from './apiClient';

export const paymentMethodApi = {
  getAll: (pageNum = 1, pageSize = 50) =>
    get('/payment-methods', { pageNum, pageSize }),
};
