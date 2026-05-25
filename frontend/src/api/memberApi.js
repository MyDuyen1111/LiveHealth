import { get } from './apiClient';

export const memberApi = {
  getAll: (pageNum = 1, pageSize = 10) =>
    get('/professional-members', { pageNum, pageSize }),
};
