import { get, post } from './apiClient';

export const reviewApi = {
  getByProductId: (productId, pageNum = 1, pageSize = 10) =>
    get(`/products/${productId}/reviews`, { pageNum, pageSize }),
  createReview: (productId, rating, comment) =>
    post(`/products/${productId}/reviews`, { rating, comment }),
};
