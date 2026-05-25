import { get, put, post, postForm } from './apiClient';

export const userApi = {
  getProfile: () => get('/user/profile'),
  updateProfile: (data) => put('/user/update-profile', data),
  uploadAvatar: (file) => {
    const fd = new FormData();
    fd.append('file', file);
    return postForm('/user/upload-avatar', fd);
  },
  fillPersonalInformation: (data) => post('/user/personal-information', data),
  updateBillingAddress: (data) => put('/user/billing-address', data),
  changePassword: (data) => put('/user/change-password', data),
};
