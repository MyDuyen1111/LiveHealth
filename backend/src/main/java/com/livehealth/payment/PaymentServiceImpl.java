package com.livehealth.payment;

import com.livehealth.shared.config.VNPayConfig;
import com.livehealth.shared.exception.VsException;
import com.livehealth.shared.security.SecurityUtils;
import io.vertx.core.http.HttpServerRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {

  VNPayConfig vnPayConfig;
  TransactionRepository transactionRepository;

  // ─────────────────────────────────────────────────────────────
  // 1. TẠO URL THANH TOÁN
  // ─────────────────────────────────────────────────────────────
  @Override
  @Transactional(rollbackOn = Exception.class)
  public String createPaymentUrl(long amount, HttpServerRequest request) {
    String vnpTxnRef = vnPayConfig.getRandomNumber(8);
    String vnpIpAddr = vnPayConfig.getIpAddress(request);
    long amountInVND = amount * 100; // VNPay yêu cầu nhân 100

    // Lấy userId nếu đã đăng nhập (không bắt buộc)
    UUID userId = null;
    try {
      userId = SecurityUtils.getCurrentUserId();
    } catch (Exception ignored) {
      // anonymous payment — bỏ qua
    }

    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")); // Docker JVM mặc định UTC
    String vnpCreateDate = formatter.format(cld.getTime());

    // Lưu giao dịch vào DB với trạng thái PENDING
    Transaction transaction = new Transaction();
    transaction.setVnpTxnRef(vnpTxnRef);
    transaction.setVnpOrderInfo("Thanh toan don hang: " + vnpTxnRef);
    transaction.setAmount(amount);
    transaction.setStatus(TransactionStatus.PENDING);
    transaction.setUserId(userId);
    transaction.setVnpTransactionDate(vnpCreateDate);
    transactionRepository.save(transaction);
    log.info("[VNPay] Created PENDING transaction: txnRef={}, amount={}", vnpTxnRef, amount);
    // Build params
    Map<String, String> vnpParams = new HashMap<>();
    vnpParams.put("vnp_Version", vnPayConfig.getVnp_Version());
    vnpParams.put("vnp_Command", vnPayConfig.getVnp_Command());
    vnpParams.put("vnp_TmnCode", vnPayConfig.getVnp_TmnCode());
    vnpParams.put("vnp_Amount", String.valueOf(amountInVND));
    vnpParams.put("vnp_CurrCode", "VND");
    vnpParams.put("vnp_BankCode", "NCB"); // Fix NCB cho Sandbox; FE có thể truyền lên
    vnpParams.put("vnp_TxnRef", vnpTxnRef);
    vnpParams.put("vnp_OrderInfo", transaction.getVnpOrderInfo());
    vnpParams.put("vnp_OrderType", "other");
    vnpParams.put("vnp_Locale", "vn");
    vnpParams.put("vnp_ReturnUrl", vnPayConfig.getVnp_ReturnUrl());
    vnpParams.put("vnp_IpAddr", vnpIpAddr);
    vnpParams.put("vnp_CreateDate", vnpCreateDate);

    cld.add(Calendar.MINUTE, 15);
    vnpParams.put("vnp_ExpireDate", formatter.format(cld.getTime()));

    return buildPaymentUrl(vnpParams);
  }

  // ─────────────────────────────────────────────────────────────
  // 2. XỬ LÝ IPN (Instant Payment Notification)
  // ─────────────────────────────────────────────────────────────
  @Override
  @Transactional(rollbackOn = Exception.class)
  public Map<String, String> handleIpn(HttpServerRequest request) {
    try {
      // Thu thập tất cả params từ VNPay
      Map<String, String> fields = extractParams(request);

      String receivedHash = request.getParam("vnp_SecureHash");
      fields.remove("vnp_SecureHashType");
      fields.remove("vnp_SecureHash");
      log.info("[VNPay IPN] Received: txnRef={}", request.getParam("vnp_TxnRef"));

      // Bước 1: Verify chữ ký
      String computedHash = hashAllFields(fields);
      if (!computedHash.equals(receivedHash)) {
        log.warn("[VNPay IPN] Invalid checksum. txnRef={}", request.getParam("vnp_TxnRef"));
        return Map.of("RspCode", "97", "Message", "Invalid Checksum");
      }

      String txnRef = request.getParam("vnp_TxnRef");
      long vnpAmount = Long.parseLong(request.getParam("vnp_Amount")); // Đã nhân 100
      String responseCode = request.getParam("vnp_ResponseCode");

      // Bước 2: Kiểm tra đơn hàng tồn tại
      Optional<Transaction> optTxn = transactionRepository.findByVnpTxnRef(txnRef);
      if (optTxn.isEmpty()) {
        log.warn("[VNPay IPN] Order not found: txnRef={}", txnRef);
        return Map.of("RspCode", "01", "Message", "Order not found");
      }

      Transaction txn = optTxn.get();

      // Bước 3: Kiểm tra số tiền (KHÔNG tin FE — lấy từ DB)
      if (txn.getAmount() * 100 != vnpAmount) {
        log.warn("[VNPay IPN] Invalid amount: expected={}, got={}", txn.getAmount() * 100, vnpAmount);
        return Map.of("RspCode", "04", "Message", "Invalid Amount");
      }

      // Bước 4: Kiểm tra trạng thái đơn (tránh xử lý lặp)
      if (txn.getStatus() != TransactionStatus.PENDING) {
        log.warn("[VNPay IPN] Order already confirmed: txnRef={}, status={}", txnRef, txn.getStatus());
        return Map.of("RspCode", "02", "Message", "Order already confirmed");
      }

      // Bước 5: Cập nhật DB
      txn.setVnpPayDate(request.getParam("vnp_PayDate"));
      txn.setVnpResponseCode(responseCode);

      if ("00".equals(responseCode)) {
        txn.setStatus(TransactionStatus.SUCCESS);
        log.info("[VNPay IPN] Transaction SUCCESS: txnRef={}", txnRef);
      } else {
        txn.setStatus(TransactionStatus.FAILED);
        log.info("[VNPay IPN] Transaction FAILED: txnRef={}, code={}", txnRef, responseCode);
      }
      transactionRepository.save(txn);

      return Map.of("RspCode", "00", "Message", "Confirm Success");

    } catch (Exception e) {
      log.error("[VNPay IPN] Unexpected error: {}", e.getMessage(), e);
      return Map.of("RspCode", "99", "Message", "Unknown error");
    }
  }

  // ─────────────────────────────────────────────────────────────
  // 3. POLLING TRẠNG THÁI GIAO DỊCH
  // ─────────────────────────────────────────────────────────────
  @Override
  public TransactionStatus getTransactionStatus(String txnRef) {
    return transactionRepository.findByVnpTxnRef(txnRef)
        .map(Transaction::getStatus)
        .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch: " + txnRef));
  }

  // ─────────────────────────────────────────────────────────────
  // 4. QUERY TRANSACTION & REFUND
  // ─────────────────────────────────────────────────────────────
  @Override
  public String queryTransaction(String txnRef, HttpServerRequest request) {
    Transaction transaction = transactionRepository.findByVnpTxnRef(txnRef)
        .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch: " + txnRef));

    String vnpRequestId = vnPayConfig.getRandomNumber(8);
    String vnpVersion = vnPayConfig.getVnp_Version();
    String vnpCommand = "querydr";
    String vnpTmnCode = vnPayConfig.getVnp_TmnCode();
    String vnpTxnRef = transaction.getVnpTxnRef();
    String vnpOrderInfo = "Kiem tra ket qua GD OrderId:" + vnpTxnRef;
    String vnpTransDate = transaction.getVnpTransactionDate();
    if (vnpTransDate == null) {
        throw new VsException(HttpStatus.BAD_REQUEST, "Giao dịch cũ không có vnpTransactionDate");
    }

    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String vnpCreateDate = formatter.format(cld.getTime());
    String vnpIpAddr = vnPayConfig.getIpAddress(request);

    String hashData = String.join("|", vnpRequestId, vnpVersion, vnpCommand, vnpTmnCode,
                                  vnpTxnRef, vnpTransDate, vnpCreateDate, vnpIpAddr, vnpOrderInfo);
    String vnpSecureHash = vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData);

    Map<String, String> params = new HashMap<>();
    params.put("vnp_RequestId", vnpRequestId);
    params.put("vnp_Version", vnpVersion);
    params.put("vnp_Command", vnpCommand);
    params.put("vnp_TmnCode", vnpTmnCode);
    params.put("vnp_TxnRef", vnpTxnRef);
    params.put("vnp_OrderInfo", vnpOrderInfo);
    params.put("vnp_TransactionDate", vnpTransDate);
    params.put("vnp_CreateDate", vnpCreateDate);
    params.put("vnp_IpAddr", vnpIpAddr);
    params.put("vnp_SecureHash", vnpSecureHash);

    try {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String json = mapper.writeValueAsString(params);

        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(vnPayConfig.getVnp_ApiUrl()))
            .header("Content-Type", "application/json")
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
            .build();
            
        java.net.http.HttpResponse<String> response = httpClient.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
        return response.body();
    } catch (Exception e) {
        throw new VsException(HttpStatus.BAD_REQUEST, "Giao dich VNPAY query fail: " + e.getMessage());
    }
  }

  @Override
  public String refundTransaction(String txnRef, long amount, String tranType, String createBy, HttpServerRequest request) {
    Transaction transaction = transactionRepository.findByVnpTxnRef(txnRef)
        .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch: " + txnRef));

    String vnpRequestId = vnPayConfig.getRandomNumber(8);
    String vnpVersion = vnPayConfig.getVnp_Version();
    String vnpCommand = "refund";
    String vnpTmnCode = vnPayConfig.getVnp_TmnCode();
    String vnpTransactionType = tranType;
    String vnpTxnRef = transaction.getVnpTxnRef();
    long amountInVnd = amount * 100;
    String vnpAmount = String.valueOf(amountInVnd);
    String vnpOrderInfo = "Hoan tien GD OrderId:" + vnpTxnRef;
    String vnpTransactionNo = ""; // Empty as per demo
    String vnpTransDate = transaction.getVnpTransactionDate();
    if (vnpTransDate == null) {
        throw new VsException(HttpStatus.BAD_REQUEST, "Giao dịch cũ không có vnpTransactionDate");
    }

    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String vnpCreateDate = formatter.format(cld.getTime());
    String vnpIpAddr = vnPayConfig.getIpAddress(request);

    String hashData = String.join("|", vnpRequestId, vnpVersion, vnpCommand, vnpTmnCode,
                                  vnpTransactionType, vnpTxnRef, vnpAmount, vnpTransactionNo,
                                  vnpTransDate, createBy, vnpCreateDate, vnpIpAddr, vnpOrderInfo);
    String vnpSecureHash = vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData);

    Map<String, String> params = new HashMap<>();
    params.put("vnp_RequestId", vnpRequestId);
    params.put("vnp_Version", vnpVersion);
    params.put("vnp_Command", vnpCommand);
    params.put("vnp_TmnCode", vnpTmnCode);
    params.put("vnp_TransactionType", vnpTransactionType);
    params.put("vnp_TxnRef", vnpTxnRef);
    params.put("vnp_Amount", vnpAmount);
    params.put("vnp_OrderInfo", vnpOrderInfo);
    params.put("vnp_TransactionDate", vnpTransDate);
    params.put("vnp_CreateBy", createBy);
    params.put("vnp_CreateDate", vnpCreateDate);
    params.put("vnp_IpAddr", vnpIpAddr);
    params.put("vnp_SecureHash", vnpSecureHash);

    try {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String json = mapper.writeValueAsString(params);

        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(vnPayConfig.getVnp_ApiUrl()))
            .header("Content-Type", "application/json")
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
            .build();
            
        java.net.http.HttpResponse<String> response = httpClient.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
        return response.body();
    } catch (Exception e) {
        throw new VsException(HttpStatus.BAD_REQUEST, "Giao dich VNPAY query fail: " + e.getMessage());
    }
  }

  // ─────────────────────────────────────────────────────────────
  // PRIVATE UTILS
  // ─────────────────────────────────────────────────────────────

  private String buildPaymentUrl(Map<String, String> vnpParams) {
    List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
    Collections.sort(fieldNames);
    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();
    boolean first = true;
    for (String fieldName : fieldNames) {
      String fieldValue = vnpParams.get(fieldName);
      if (fieldValue != null && !fieldValue.isEmpty()) {
        try {
          String encodedKey = URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()).replace("+", "%20");
          String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()).replace("+", "%20");

          if (!first) {
            query.append('&');
            hashData.append('&');
          }

          hashData.append(fieldName).append('=').append(encodedValue);
          query.append(encodedKey).append('=').append(encodedValue);

          first = false;
        } catch (Exception e) {
          log.error("Encoding error in buildPaymentUrl: ", e);
        }
      }
    }
    String secureHash = vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
    return vnPayConfig.getVnp_PayUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;
  }

  private Map<String, String> extractParams(HttpServerRequest request) {
    Map<String, String> fields = new HashMap<>();
    for (Map.Entry<String, String> entry : request.params()) {
      String name = entry.getKey();
      String value = entry.getValue();
      if (value != null && !value.isEmpty()) {
        fields.put(name, value);
      }
    }
    return fields;
  }

  private String hashAllFields(Map<String, String> fields) {
    List<String> fieldNames = new ArrayList<>(fields.keySet());
    Collections.sort(fieldNames);
    StringBuilder hashData = new StringBuilder();
    boolean first = true;
    for (String fieldName : fieldNames) {
      String fieldValue = fields.get(fieldName);
      if (fieldValue != null && !fieldValue.isEmpty()) {
        try {
          String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()).replace("+", "%20");
          if (!first) {
            hashData.append('&');
          }
          hashData.append(fieldName).append('=').append(encodedValue);
          first = false;
        } catch (Exception e) {
          log.error("Encoding error in hashAllFields: ", e);
        }
      }
    }
    return vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
  }
}
