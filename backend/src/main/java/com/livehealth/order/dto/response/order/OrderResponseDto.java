package com.livehealth.order.dto.response.order;

import com.livehealth.order.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponseDto {

    UUID id;

    String orderNumber;

    UUID userId;

    String userFullName;

    LocalDateTime orderDate;

    OrderStatus status;

    UUID paymentMethodId;

    String paymentMethodName;

    double subtotal;

    double discountPercentage;

    double shippingFee;

    double totalAmount;

    String contactEmail;

    String contactPhone;

    AddressResponseDto billingAddress;

    AddressResponseDto shippingAddress;

    List<OrderItemResponseDto> orderItems;

    String note;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
