import { get } from './apiClient';

export const promotionApi = {
  getByProductId: (productId) =>
    get(`/products/${productId}/promotions`),
};
