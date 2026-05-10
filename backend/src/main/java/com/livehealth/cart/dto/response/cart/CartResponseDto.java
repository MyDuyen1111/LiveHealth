package com.livehealth.cart.dto.response.cart;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponseDto {

    UUID id;

    List<CartItemResponseDto> items;

    UUID appliedCouponId;

    String appliedCouponCode;

    Double couponDiscountValue; // Could be amount or percentage value

    Boolean isCouponPercentage;

    ShippingMethodResponseDto shippingMethod;

    PaymentMethodResponseDto paymentMethod;

    double itemsTotalAmount;

    double finalTotalAmount;
}
