import { post, get } from './apiClient';

export const paymentApi = {
  // POST /payment/create-payment?amount=xxx  → trả về URL thanh toán VNPay
  createPayment: (amount) =>
    post(`/payment/create-payment?amount=${amount}`),

  // GET /payment/status/{txnRef}  → polling trạng thái giao dịch
  getPaymentStatus: (txnRef) =>
    get(`/payment/status/${txnRef}`),

  // GET /vnpay/query/{txnRef}  → truy vấn trực tiếp VNPay
  queryTransaction: (txnRef) =>
    get(`/vnpay/query/${txnRef}`),
};
