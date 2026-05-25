import { get } from './apiClient';

export const productAttributeApi = {
  getByProductId: (productId) =>
    get(`/products/${productId}/attributes`),
};
