import { post } from './apiClient';

export const authApi = {
  login: (email, password) => post('/auth/login', { email, password }),
  register: (payload) => post('/auth/register', payload),
  forgotPassword: (email) => post('/auth/forgot-password', { email }),
  verifyOtpResetPassword: (email, otp) => post('/auth/verify-otp-to-reset-password', { email, otp }),
  resetPassword: (email, newPassword, reEnterPassword) =>
    post('/auth/reset-password', { email, newPassword, reEnterPassword }),
  refresh: (refreshToken) => post('/auth/refresh', { refreshToken }),
  logout: (token) => post('/auth/logout', { token }),
};
