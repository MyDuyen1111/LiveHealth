package com.livehealth.payment;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.livehealth.shared.config.VNPayConfig;

@RestApiV1
@Path("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

  PaymentService paymentService;
  VNPayConfig vnPayConfig; // Chỉ dùng cho Return URL signature verify (không có business logic)

  // 1. TẠO URL THANH TOÁN
  @Operation(summary = "Tạo URL thanh toán VNPay", description = "Lưu giao dịch (PENDING) vào DB và trả về URL thanh toán VNPay", security = @SecurityRequirement(name = "Bearer Token"))
  @POST
  @Path(UrlConstant.Payment.CREATE_PAYMENT)
  public Response createPayment(
      @Context HttpServletRequest request,
      @QueryParam("amount") long amount) throws Exception {

    String paymentUrl = paymentService.createPaymentUrl(amount, request);
    return VsResponseUtil.success(paymentUrl);
  }

  // 2. RETURN URL — VNPay redirect người dùng về sau thanh toán
  // Chỉ dùng để hiển thị UI, KHÔNG update DB (theo khuyến cáo VNPay)
  @Operation(summary = "VNPay Return URL", description = "VNPay redirect về đây sau thanh toán. Chỉ verify chữ ký để hiển thị kết quả UI, không update DB.")
  @GET
  @Path(UrlConstant.Payment.PAYMENT_RETURN)
  public Response paymentReturn(@Context HttpServletRequest request) {
    Map<String, String> fields = extractParams(request);

    String vnpSecureHash = request.getParameter("vnp_SecureHash");
    fields.remove("vnp_SecureHashType");
    fields.remove("vnp_SecureHash");

    String signValue = hashAllFields(fields);
    if (!signValue.equals(vnpSecureHash)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Chữ ký không hợp lệ").build();
    }

    if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
      return Response.ok("Giao dịch thành công").build();
    } else {
      return Response.status(Response.Status.BAD_REQUEST).entity("Giao dịch thất bại").build();
    }
  }

  // 3. IPN — VNPay gọi ngầm (Server-to-Server) để update DB
  @Operation(summary = "VNPay IPN Webhook", description = "VNPay gọi ngầm để thông báo kết quả thanh toán. Xác minh chữ ký và cập nhật DB.")
  @GET
  @Path(UrlConstant.Payment.PAYMENT_IPN)
  public Response paymentIpn(@Context HttpServletRequest request) {
    return Response.ok(paymentService.handleIpn(request)).build();
  }

  // 4. POLLING — FE dùng để kiểm tra trạng thái giao dịch
  @Operation(summary = "Kiểm tra trạng thái giao dịch", description = "FE polling để lấy trạng thái giao dịch sau khi được redirect về từ VNPay", security = @SecurityRequirement(name = "Bearer Token"))
  @GET
  @Path(UrlConstant.Payment.PAYMENT_STATUS)
  public Response getPaymentStatus(
      @PathParam("txnRef") String txnRef) {

    return VsResponseUtil.success(paymentService.getTransactionStatus(txnRef));
  }

  // 5. TRUY VẤN VNPay (Server to VNPay)
  @Operation(summary = "Truy vấn trạng thái giao dịch trực tiếp từ VNPay API", security = @SecurityRequirement(name = "Bearer Token"))
  @GET
  @Path("/vnpay/query/{txnRef}")
  public Response queryTransaction(
      @PathParam("txnRef") String txnRef,
      @Context HttpServletRequest request) {
    return Response.ok(paymentService.queryTransaction(txnRef, request)).build();
  }

  // 6. HOÀN TIỀN (Server to VNPay)
  @Operation(summary = "Hoàn tiền giao dịch VNPay", security = @SecurityRequirement(name = "Bearer Token"))
  @POST
  @Path("/vnpay/refund/{txnRef}")
  public Response refundTransaction(
      @PathParam("txnRef") String txnRef,
      @QueryParam("amount") long amount,
      @QueryParam("tranType") @DefaultValue("02") String tranType, // 02: Toàn phần, 03: Một phần
      @Context HttpServletRequest request) {
    String createBy = "admin"; // Lấy từ auth user trong thực tế
    return Response.ok(paymentService.refundTransaction(txnRef, amount, tranType, createBy, request)).build();
  }

  // ─── PRIVATE UTILS (chỉ dùng cho Return URL) ───────────────────────────
  private Map<String, String> extractParams(HttpServletRequest request) {
    Map<String, String> fields = new HashMap<>();
    for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
      String name = params.nextElement();
      String value = request.getParameter(name);
      if (value != null && !value.isEmpty())
        fields.put(name, value);
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
          // ignore
        }
      }
    }
    return vnPayConfig.hmacSHA512(vnPayConfig.secretKey, hashData.toString());
  }
}