package com.livehealth.cart;

import com.livehealth.cart.dto.request.cart.CartItemRequestDto;
import com.livehealth.cart.dto.request.cart.UpdateCartItemRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.cart.dto.response.cart.CartResponseDto;

import java.util.UUID;

public interface CartService {

    CartResponseDto getMyCart();

    CartResponseDto addItem(CartItemRequestDto request);

    CartResponseDto updateItem(UUID itemId, UpdateCartItemRequestDto request);

    CommonResponseDto removeItem(UUID itemId);

    CommonResponseDto clearCart();

    CartResponseDto applyPromotion(UUID promotionId);

    CartResponseDto selectShippingMethod(UUID shippingMethodId);

    CartResponseDto selectPaymentMethod(UUID paymentMethodId);
}
