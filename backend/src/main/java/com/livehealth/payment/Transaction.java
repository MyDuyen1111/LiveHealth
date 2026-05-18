package com.livehealth.payment;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false)
  UUID id;

  @Column(nullable = false, unique = true)
  String vnpTxnRef; // Mã giao dịch do hệ thống tạo (unique)

  @Column(nullable = false)
  String vnpOrderInfo;

  @Column(nullable = false)
  Long amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  TransactionStatus status;

  @Column
  String vnpPayDate; // Ngày giờ thanh toán (null trước khi thanh toán xong)

  @Column
  String vnpResponseCode; // Mã phản hồi từ VNPay (null trước khi IPN về)

  @Column
  UUID userId; // ID người dùng tạo giao dịch (nullable vì IPN endpoint public)

  @Column
  String vnpTransactionDate; // Lưu trữ vnp_CreateDate gửi sang VNPay để dùng cho Query và Refund

  @Column(nullable = false, updatable = false)
  LocalDateTime createdAt;

  @Column(nullable = false)
  LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
