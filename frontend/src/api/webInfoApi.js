import { get } from './apiClient';

export const webInfoApi = {
  get: () => get('/web-information'),
};
