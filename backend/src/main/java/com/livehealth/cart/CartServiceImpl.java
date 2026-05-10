package com.livehealth.cart;

import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.cart.dto.request.cart.CartItemRequestDto;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.ProductPromotionRepository;
import com.livehealth.order.ShippingMethodRepository;
import com.livehealth.order.PaymentMethodRepository;
import com.livehealth.cart.dto.request.cart.UpdateCartItemRequestDto;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.ProductPromotionRepository;
import com.livehealth.order.ShippingMethodRepository;
import com.livehealth.order.PaymentMethodRepository;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.cart.dto.response.cart.CartResponseDto;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.ProductPromotionRepository;
import com.livehealth.order.ShippingMethodRepository;
import com.livehealth.order.PaymentMethodRepository;
import com.livehealth.cart.Cart;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.ProductPromotionRepository;
import com.livehealth.order.ShippingMethodRepository;
import com.livehealth.order.PaymentMethodRepository;
import com.livehealth.cart.CartItem;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.ProductPromotionRepository;
import com.livehealth.order.ShippingMethodRepository;
import com.livehealth.order.PaymentMethodRepository;
import com.livehealth.cart.PaymentMethod;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.ProductPromotionRepository;
import com.livehealth.order.ShippingMethodRepository;
import com.livehealth.order.PaymentMethodRepository;
import com.livehealth.cart.ShippingMethod;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.ProductPromotionRepository;
import com.livehealth.order.ShippingMethodRepository;
import com.livehealth.order.PaymentMethodRepository;
import com.livehealth.product.Product;
import com.livehealth.product.ProductPromotion;
import com.livehealth.user.User;
import com.livehealth.cart.CartMapper;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.ProductPromotionRepository;
import com.livehealth.order.ShippingMethodRepository;
import com.livehealth.order.PaymentMethodRepository;
import com.livehealth.shared.exception.VsException;
import com.livehealth.user.*;
import com.livehealth.shared.security.SecurityUtils;
import com.livehealth.cart.CartService;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.ProductPromotionRepository;
import com.livehealth.order.ShippingMethodRepository;
import com.livehealth.order.PaymentMethodRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {

    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    ProductRepository productRepository;
    ProductPromotionRepository productPromotionRepository;
    ShippingMethodRepository shippingMethodRepository;
    PaymentMethodRepository paymentMethodRepository;
    UserRepository userRepository;
    CartMapper cartMapper;

    private Cart getOrCreateCurrentUserCart() {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.User.ERR_USER_NOT_EXISTED));

        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = Cart.builder()
                    .user(user)
                    .items(new ArrayList<>())
                    .updatedAt(LocalDateTime.now())
                    .build();
            return cartRepository.save(newCart);
        });
    }

    @Override
    public CartResponseDto getMyCart() {
        Cart cart = getOrCreateCurrentUserCart();
        return cartMapper.toResponseDto(cart);
    }

    @Override
    public CartResponseDto addItem(CartItemRequestDto request) {
        Cart cart = getOrCreateCurrentUserCart();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Product.ERR_PRODUCT_NOT_FOUND));

        if (product.getStock() <= 0) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Cart.ERR_PRODUCT_OUT_OF_STOCK);
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        int existingQuantity = existingItemOpt.map(CartItem::getQuantity).orElse(0);

        if (existingQuantity + request.getQuantity() > product.getStock()) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Cart.ERR_PRODUCT_QUANTITY_EXCEEDED);
        }

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            existingItem.setPrice(product.getOldPrice()); // Update price potentially
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .price(product.getOldPrice())
                    .build();
            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return cartMapper.toResponseDto(cart);
    }

    @Override
    public CartResponseDto updateItem(UUID itemId, UpdateCartItemRequestDto request) {
        Cart cart = getOrCreateCurrentUserCart();

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Cart.ERR_CART_ITEM_NOT_FOUND));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new VsException(HttpStatus.FORBIDDEN, "Item does not belong to your cart");
        }

        if (item.getProduct().getStock() <= 0) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Cart.ERR_PRODUCT_OUT_OF_STOCK);
        }

        if (request.getQuantity() > item.getProduct().getStock()) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Cart.ERR_PRODUCT_QUANTITY_EXCEEDED);
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return cartMapper.toResponseDto(cart);
    }

    @Override
    public CommonResponseDto removeItem(UUID itemId) {
        Cart cart = getOrCreateCurrentUserCart();

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Cart.ERR_CART_ITEM_NOT_FOUND));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new VsException(HttpStatus.FORBIDDEN, "Item does not belong to your cart");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.Cart.REMOVE_ITEM_SUCCESS);
    }

    @Override
    public CommonResponseDto clearCart() {
        Cart cart = getOrCreateCurrentUserCart();

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setAppliedCoupon(null);
        cart.setShippingMethod(null);
        cart.setPaymentMethod(null);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.Cart.CLEAR_CART_SUCCESS);
    }

    @Override
    public CartResponseDto applyPromotion(UUID promotionId) {
        Cart cart = getOrCreateCurrentUserCart();

        ProductPromotion promotion = productPromotionRepository.findById(promotionId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ProductPromotion.ERR_PROMOTION_NOT_FOUND));

        if (!promotion.isActive()) {
            throw new VsException(HttpStatus.BAD_REQUEST, "Promotion is no longer active");
        }

        cart.setAppliedCoupon(promotion);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return cartMapper.toResponseDto(cart);
    }

    @Override
    public CartResponseDto selectShippingMethod(UUID shippingMethodId) {
        Cart cart = getOrCreateCurrentUserCart();

        ShippingMethod shippingMethod = shippingMethodRepository.findById(shippingMethodId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ShippingMethod.ERR_SHIPPING_METHOD_NOT_FOUND));

        if (!shippingMethod.isActive()) {
            throw new VsException(HttpStatus.BAD_REQUEST, "Shipping method is not available");
        }

        cart.setShippingMethod(shippingMethod);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return cartMapper.toResponseDto(cart);
    }

    @Override
    public CartResponseDto selectPaymentMethod(UUID paymentMethodId) {
        Cart cart = getOrCreateCurrentUserCart();

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.PaymentMethod.ERR_PAYMENT_METHOD_NOT_FOUND));

        if (!paymentMethod.isActive()) {
            throw new VsException(HttpStatus.BAD_REQUEST, "Payment method is not available");
        }

        cart.setPaymentMethod(paymentMethod);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return cartMapper.toResponseDto(cart);
    }
}
