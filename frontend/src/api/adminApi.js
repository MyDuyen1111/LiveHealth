import { get, post, put, del, postForm } from './apiClient';

// ─── Products ───
export const adminProductApi = {
  getAll: (pageNum = 1, pageSize = 12) => get('/products', { pageNum, pageSize }),
  getById: (id) => get(`/products/${id}`),
  create: (data) => post('/products', data),
  update: (id, data) => put(`/products/${id}`, data),
  delete: (id) => del(`/products/${id}`),
  uploadImages: (id, files) => {
    const fd = new FormData();
    files.forEach(f => fd.append('file', f));  // backend expects 'file' (single key, multiple values)
    return postForm(`/products/${id}/images`, fd);
  },
  addTags: (id, tagIds) => post(`/products/${id}/tags`, tagIds),
  removeTag: (id, tagId) => del(`/products/${id}/tags/${tagId}`),
};

// ─── Categories ───
export const adminCategoryApi = {
  getAll: (pageNum = 1, pageSize = 50) => get('/categories', { pageNum, pageSize }),
  getById: (id) => get(`/categories/${id}`),
  create: (data) => post('/categories', data),
  update: (id, data) => put(`/categories/${id}`, data),
  delete: (id) => del(`/categories/${id}`),
};

// ─── Brands ───
export const adminBrandApi = {
  getAll: (pageNum = 1, pageSize = 50) => get('/brands', { pageNum, pageSize }),
  getById: (id) => get(`/brands/${id}`),
  create: (data) => post('/brands', data),
  update: (id, data) => put(`/brands/${id}`, data),
  delete: (id) => del(`/brands/${id}`),
  uploadLogo: (id, file) => {
    const fd = new FormData();
    fd.append('file', file);
    return postForm(`/brands/${id}/logo`, fd);
  },
};

// ─── Tags ───
export const adminTagApi = {
  getAll: (pageNum = 1, pageSize = 50) => get('/tags', { pageNum, pageSize }),
  getById: (id) => get(`/tags/${id}`),
  create: (data) => post('/tags', data),
  update: (id, data) => put(`/tags/${id}`, data),
  delete: (id) => del(`/tags/${id}`),
};

// ─── News ───
export const adminNewsApi = {
  getAll: (pageNum = 1, pageSize = 12) => get('/news', { pageNum, pageSize }),
  getById: (id) => get(`/news/${id}`),
  create: (data) => post('/news', data),
  update: (id, data) => put(`/news/${id}`, data),
  delete: (id) => del(`/news/${id}`),
  uploadThumbnail: (id, file) => {
    const fd = new FormData();
    fd.append('file', file);
    return postForm(`/news/${id}/thumbnail`, fd);
  },
  addTags: (id, tagIds) => post(`/news/${id}/tags`, tagIds),
  removeTag: (id, tagId) => del(`/news/${id}/tags/${tagId}`),
};

// ─── News Categories ───
export const adminNewsCategoryApi = {
  getAll: (pageNum = 1, pageSize = 50) => get('/news-categories', { pageNum, pageSize }),
  create: (data) => post('/news-categories', data),
  update: (id, data) => put(`/news-categories/${id}`, data),
  delete: (id) => del(`/news-categories/${id}`),
};

// ─── News Tags ───
export const adminNewsTagApi = {
  getAll: (pageNum = 1, pageSize = 50) => get('/news-tags', { pageNum, pageSize }),
  create: (data) => post('/news-tags', data),
  update: (id, data) => put(`/news-tags/${id}`, data),
  delete: (id) => del(`/news-tags/${id}`),
};

// ─── Testimonials ───
export const adminTestimonialApi = {
  getAll: (pageNum = 1, pageSize = 20) => get('/client-testimonials', { pageNum, pageSize }),
  getById: (id) => get(`/client-testimonials/${id}`),
  create: (data) => post('/client-testimonials', data),
  update: (id, data) => put(`/client-testimonials/${id}`, data),
  delete: (id) => del(`/client-testimonials/${id}`),
  uploadAvatar: (id, file) => {
    const fd = new FormData();
    fd.append('file', file);
    return postForm(`/client-testimonials/${id}/avatar`, fd);
  },
};

// ─── Professional Members ───
export const adminMemberApi = {
  getAll: (pageNum = 1, pageSize = 20) => get('/professional-members', { pageNum, pageSize }),
  getById: (id) => get(`/professional-members/${id}`),
  create: (data) => post('/professional-members', data),
  update: (id, data) => put(`/professional-members/${id}`, data),
  delete: (id) => del(`/professional-members/${id}`),
  uploadAvatar: (id, file) => {
    const fd = new FormData();
    fd.append('file', file);
    return postForm(`/professional-members/${id}/avatar`, fd);
  },
};

// ─── Orders ───
export const adminOrderApi = {
  getAll: (pageNum = 1, pageSize = 20) => get('/orders', { pageNum, pageSize }),
  getById: (id) => get(`/orders/${id}`),
  updateStatus: (id, status) => put(`/orders/${id}/status`, { status }),
};

// ─── Shipping Methods ───
export const adminShippingApi = {
  getAll: (pageNum = 1, pageSize = 50) => get('/shipping-methods', { pageNum, pageSize }),
  create: (data) => post('/shipping-methods', data),
  update: (id, data) => put(`/shipping-methods/${id}`, data),
  delete: (id) => del(`/shipping-methods/${id}`),
};

// ─── Payment Methods ───
export const adminPaymentApi = {
  getAll: (pageNum = 1, pageSize = 50) => get('/payment-methods', { pageNum, pageSize }),
  create: (data) => post('/payment-methods', data),
  update: (id, data) => put(`/payment-methods/${id}`, data),
  delete: (id) => del(`/payment-methods/${id}`),
};

// ─── Product Attributes ───
export const adminProductAttributeApi = {
  getByProduct: (productId) => get(`/products/${productId}/attributes`),
  create: (productId, data) => post(`/products/${productId}/attributes`, data),
  update: (id, data) => put(`/product-attributes/${id}`, data),
  delete: (id) => del(`/product-attributes/${id}`),
};

// ─── Product Promotions ───
export const adminProductPromotionApi = {
  getByProduct: (productId) => get(`/products/${productId}/promotions`),
  create: (productId, data) => post(`/products/${productId}/promotions`, data),
  update: (id, data) => put(`/product-promotions/${id}`, data),
  delete: (id) => del(`/product-promotions/${id}`),
};

// ─── Reviews (admin) ───
export const adminReviewApi = {
  getByProduct: (productId, pageNum = 1, pageSize = 20) =>
    get(`/products/${productId}/reviews`, { pageNum, pageSize }),
  delete: (id) => del(`/reviews/${id}`),
};

// ─── Web Information ───
export const adminWebInfoApi = {
  get: () => get('/web-information'),
  update: (data) => put('/web-information', data),
  uploadLogo: (file) => {
    const fd = new FormData();
    fd.append('file', file);
    return postForm('/web-information/logo', fd);
  },
};
