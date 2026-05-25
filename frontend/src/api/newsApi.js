import { get, post } from './apiClient';

export const newsApi = {
  getAll: (pageNum = 1, pageSize = 6, categoryId, tagId) =>
    get('/news', { pageNum, pageSize, categoryId, tagId }),
  getById: (id) => get(`/news/${id}`),
  getComments: (newsId, pageNum = 1, pageSize = 10) =>
    get(`/news/${newsId}/comments`, { pageNum, pageSize }),
  createComment: (newsId, content) =>
    post(`/news/${newsId}/comments`, { content }),
  getCategoryCounts: () => get('/news/category-counts'),
};

export const newsCategoryApi = {
  getAll: (pageNum = 1, pageSize = 50) =>
    get('/news-categories', { pageNum, pageSize }),
};

export const newsTagApi = {
  getAll: (pageNum = 1, pageSize = 50) =>
    get('/news-tags', { pageNum, pageSize }),
};
