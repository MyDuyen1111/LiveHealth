package com.livehealth.cart;

import com.livehealth.cart.dto.response.cart.CartItemResponseDto;
import com.livehealth.order.ShippingMethodMapper;
import com.livehealth.order.PaymentMethodMapper;
import com.livehealth.cart.dto.response.cart.CartResponseDto;
import com.livehealth.order.ShippingMethodMapper;
import com.livehealth.order.PaymentMethodMapper;
import com.livehealth.cart.Cart;
import com.livehealth.order.ShippingMethodMapper;
import com.livehealth.order.PaymentMethodMapper;
import com.livehealth.cart.CartItem;
import com.livehealth.order.ShippingMethodMapper;
import com.livehealth.order.PaymentMethodMapper;
import com.livehealth.product.ProductPromotion;
import org.mapstruct.*;

@Mapper(componentModel = "cdi", uses = { ShippingMethodMapper.class,
        PaymentMethodMapper.class }, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CartMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", expression = "java(cartItem.getProduct().getImageUrl() != null && !cartItem.getProduct().getImageUrl().isEmpty() ? cartItem.getProduct().getImageUrl().get(0) : null)")
    @Mapping(target = "subtotal", expression = "java(cartItem.getPrice() * cartItem.getQuantity())")
    CartItemResponseDto cartItemToResponseDto(CartItem cartItem);

    @Mapping(target = "appliedCouponId", source = "appliedCoupon.id")
    @Mapping(target = "appliedCouponCode", source = "appliedCoupon.name")
    @Mapping(target = "couponDiscountValue", source = "appliedCoupon.discountValue")
    @Mapping(target = "isCouponPercentage", expression = "java(cart.getAppliedCoupon() != null && \"PERCENTAGE\".equalsIgnoreCase(cart.getAppliedCoupon().getDiscountType()))")
    @Mapping(target = "itemsTotalAmount", ignore = true)
    @Mapping(target = "finalTotalAmount", ignore = true)
    CartResponseDto toResponseDto(Cart cart);

    @AfterMapping
    default void calculateTotals(Cart cart, @MappingTarget CartResponseDto responseDto) {
        if (cart.getItems() != null) {
            double itemsTotal = cart.getItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            responseDto.setItemsTotalAmount(itemsTotal);

            double finalTotal = itemsTotal;

            // Apply promotion if any
            ProductPromotion promotion = cart.getAppliedCoupon();
            if (promotion != null && promotion.isActive()) {
                if ("PERCENTAGE".equalsIgnoreCase(promotion.getDiscountType())) {
                    finalTotal -= finalTotal * (promotion.getDiscountValue() / 100.0);
                } else {
                    finalTotal -= promotion.getDiscountValue();
                }
            }

            // Apply shipping cost
            if (cart.getShippingMethod() != null && itemsTotal > 0) {
                finalTotal += cart.getShippingMethod().getPrice();
            }

            // Set lower bound to 0
            if (itemsTotal == 0) {
                finalTotal = 0;
            } else if (finalTotal < 0) {
                finalTotal = 0;
            }

            responseDto.setFinalTotalAmount(finalTotal);
        }
    }
}
