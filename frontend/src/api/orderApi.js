import { get, post, put } from './apiClient';

export const orderApi = {
  getMyOrders: (pageNum = 1, pageSize = 10) =>
    get('/orders/me', { pageNum, pageSize }),
  getOrderById: (id) => get(`/orders/${id}`),
  placeOrder: (data) => post('/orders', data),
  cancelOrder: (id) => put(`/orders/${id}/cancel`),
};
