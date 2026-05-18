package com.livehealth.payment;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {

  /**
   * Tạo VNPay payment URL, lưu Transaction(PENDING) vào DB.
   *
   * @param amount  Số tiền (VND, chưa nhân 100)
   * @param request HTTP request để lấy IP
   * @return URL thanh toán VNPay
   */
  String createPaymentUrl(long amount, HttpServletRequest request);

  /**
   * Xử lý IPN callback từ VNPay (Server-to-Server).
   * Xác minh chữ ký, kiểm tra đơn hàng, cập nhật trạng thái DB.
   *
   * @param request HTTP request chứa params IPN
   * @return Map chứa RspCode và Message theo chuẩn VNPay
   */
  Map<String, String> handleIpn(HttpServletRequest request);

  /**
   * Lấy trạng thái hiện tại của giao dịch (dùng cho FE polling).
   *
   * @param txnRef Mã giao dịch (vnp_TxnRef)
   * @return Trạng thái giao dịch
   */
  TransactionStatus getTransactionStatus(String txnRef);
  /**
   * Truy vấn trạng thái giao dịch từ hệ thống VNPay.
   *
   * @param txnRef Mã giao dịch
   * @param request HTTP request
   * @return json response từ VNPay
   */
  String queryTransaction(String txnRef, HttpServletRequest request);

  /**
   * Hoàn tiền giao dịch trên hệ thống VNPay.
   *
   * @param txnRef Mã giao dịch
   * @param amount Số tiền (VND, chưa nhân 100)
   * @param tranType Loại hoàn tiền ("02" = Toàn phần, "03" = Một phần)
   * @param createBy User thực hiện hoàn tiền
   * @param request HTTP request
   * @return json response từ VNPay
   */
  String refundTransaction(String txnRef, long amount, String tranType, String createBy, HttpServletRequest request);
}
