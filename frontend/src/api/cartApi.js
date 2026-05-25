import { get, post, put, del } from './apiClient';

export const cartApi = {
  getMyCart: () => get('/cart'),
  addItem: (productId, quantity = 1) =>
    post('/cart/items', { productId, quantity }),
  updateItem: (itemId, quantity) =>
    put(`/cart/items/${itemId}`, { quantity }),
  removeItem: (itemId) => del(`/cart/items/${itemId}`),
  clearCart: () => del('/cart/clear'),
  selectShippingMethod: (shippingMethodId) =>
    put(`/cart/shipping-method/${shippingMethodId}`),
  selectPaymentMethod: (paymentMethodId) =>
    put(`/cart/payment-method/${paymentMethodId}`),
  applyPromotion: (promotionId) =>
    put(`/cart/promotion/${promotionId}`),
};
