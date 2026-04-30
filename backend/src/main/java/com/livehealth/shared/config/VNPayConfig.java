package com.livehealth.shared.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@ApplicationScoped
public class VNPayConfig {
  @Inject
  @ConfigProperty(name = "vnpay.tmn-code")
  public String vnp_TmnCode;

  @Inject
  @ConfigProperty(name = "vnpay.hash-secret")
  public String secretKey;

  @Inject
  @ConfigProperty(name = "vnpay.url")
  public String vnp_PayUrl;

  @Inject
  @ConfigProperty(name = "vnpay.api-url")
  public String vnp_ApiUrl;

  @Inject
  @ConfigProperty(name = "vnpay.return-url")
  public String vnp_ReturnUrl;

  @Inject
  @ConfigProperty(name = "vnpay.version")
  public String vnp_Version;

  @Inject
  @ConfigProperty(name = "vnpay.command")
  public String vnp_Command;

  // Code chuẩn của VNPAY để tạo mã băm HmacSHA512
  public String hmacSHA512(final String key, final String data) {
    try {
      if (key == null || data == null) {
        throw new NullPointerException();
      }
      final Mac hmac512 = Mac.getInstance("HmacSHA512");
      byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
      final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
      hmac512.init(secretKey);
      byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
      byte[] result = hmac512.doFinal(dataBytes);
      StringBuilder sb = new StringBuilder(2 * result.length);
      for (byte b : result) {
        sb.append(String.format("%02x", b & 0xff));
      }
      return sb.toString();
    } catch (Exception ex) {
      return "";
    }
  }

  public String getIpAddress(HttpServletRequest request) {
    String ipAdress;
    try {
      ipAdress = request.getHeader("X-FORWARDED-FOR");
      if (ipAdress == null) {
        ipAdress = request.getRemoteAddr();
      }
    } catch (Exception e) {
      ipAdress = "Invalid IP";
    }
    return ipAdress;
  }

  public String getRandomNumber(int len) {
    Random rnd = new Random();
    String chars = "0123456789";
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      sb.append(chars.charAt(rnd.nextInt(chars.length())));
    }
    return sb.toString();
  }
}