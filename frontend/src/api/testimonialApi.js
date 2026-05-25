import { get } from './apiClient';

export const testimonialApi = {
  getAll: (pageNum = 1, pageSize = 10) =>
    get('/client-testimonials', { pageNum, pageSize }),
};
