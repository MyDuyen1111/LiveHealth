package com.livehealth.cart.dto.response.cart;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponseDto {

    UUID id;

    UUID productId;

    String productName;

    String productImageUrl;

    int quantity;

    double price;

    double subtotal;
}
