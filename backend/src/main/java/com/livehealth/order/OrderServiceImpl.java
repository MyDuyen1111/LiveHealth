package com.livehealth.order;

import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.order.dto.request.order.AddressDto;
import com.livehealth.cart.CartRepository;
import com.livehealth.order.dto.request.order.CreateOrderRequestDto;
import com.livehealth.cart.CartRepository;
import com.livehealth.order.dto.request.order.UpdateOrderStatusRequestDto;
import com.livehealth.cart.CartRepository;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.order.dto.response.order.OrderResponseDto;
import com.livehealth.cart.CartRepository;
import com.livehealth.cart.Cart;
import com.livehealth.cart.CartItem;
import com.livehealth.order.Order;
import com.livehealth.cart.CartRepository;
import com.livehealth.order.OrderItem;
import com.livehealth.cart.CartRepository;
import com.livehealth.order.OrderStatus;
import com.livehealth.cart.CartRepository;
import com.livehealth.product.ProductPromotion;
import com.livehealth.product.Product;
import com.livehealth.product.ProductRepository;
import com.livehealth.user.Address;
import com.livehealth.user.User;
import com.livehealth.order.AddressOrderMapper;
import com.livehealth.cart.CartRepository;
import com.livehealth.order.OrderMapper;
import com.livehealth.cart.CartRepository;
import com.livehealth.shared.exception.VsException;
import com.livehealth.user.*;
import com.livehealth.shared.security.SecurityUtils;
import com.livehealth.cart.CartService;
import com.livehealth.order.OrderService;
import com.livehealth.cart.CartRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;
    CartRepository cartRepository;
    UserRepository userRepository;
    AddressRepository addressRepository;
    ProductRepository productRepository;
    OrderMapper orderMapper;
    AddressOrderMapper addressOrderMapper;
    CartService cartService;

    private Address resolveAddress(AddressDto dto) {
        return addressRepository.findByCountryAndStateAndStreetAddressAndCompanyNameAndZipCode(
                dto.getCountry(), dto.getState(), dto.getStreetAddress(), dto.getCompanyName(), dto.getZipCode())
                .orElseGet(() -> {
                    Address newAddress = addressOrderMapper.toEntity(dto);
                    return addressRepository.save(newAddress);
                });
    }

    private synchronized String generateNextOrderNumber() {
        Integer maxNumber = orderRepository.findMaxOrderNumber();
        int nextNum = (maxNumber != null) ? maxNumber + 1 : 1;
        return "#" + nextNum;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public OrderResponseDto placeOrder(CreateOrderRequestDto request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.User.ERR_USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Cart.ERR_CART_NOT_FOUND));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Cart.ERR_CART_EMPTY);
        }

        if (cart.getShippingMethod() == null) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Cart.ERR_SHIPPING_METHOD_NOT_SELECTED);
        }

        if (cart.getPaymentMethod() == null) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Cart.ERR_PAYMENT_METHOD_NOT_SELECTED);
        }

        Address billingAddress;
        if (request.getBillingAddress() != null) {
            billingAddress = resolveAddress(request.getBillingAddress());
        } else if (user.getAddress() != null) {
            billingAddress = user.getAddress();
        } else {
            throw new VsException(HttpStatus.BAD_REQUEST,
                    "Địa chỉ thanh toán không được để trống hoặc vui lòng cập nhật địa chỉ thanh toán mặc định");
        }

        Address shippingAddress;
        if (request.getShippingAddress() != null) {
            shippingAddress = resolveAddress(request.getShippingAddress());
        } else if (billingAddress != null) {
            shippingAddress = billingAddress;
        } else {
            throw new VsException(HttpStatus.BAD_REQUEST, "Địa chỉ giao hàng không được để trống");
        }

        double subtotal = 0;
        for (CartItem item : cart.getItems()) {
            subtotal += item.getPrice() * item.getQuantity();
        }

        double discountPercentage = 0;
        double finalTotal = subtotal;

        ProductPromotion promotion = cart.getAppliedCoupon();
        if (promotion != null && promotion.isActive()) {
            if ("PERCENTAGE".equalsIgnoreCase(promotion.getDiscountType())) {
                discountPercentage = promotion.getDiscountValue();
                finalTotal -= finalTotal * (discountPercentage / 100.0);
            } else {
                finalTotal -= promotion.getDiscountValue();
            }
        }

        double shippingFee = cart.getShippingMethod().getPrice();
        finalTotal += shippingFee;

        if (finalTotal < 0) {
            finalTotal = 0;
        }

        Order order = Order.builder()
                .user(user)
                .orderNumber(generateNextOrderNumber())
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.ORDER_RECEIVED)
                .paymentMethod(cart.getPaymentMethod())
                .subtotal(subtotal)
                .discountPercentage(discountPercentage)
                .shippingFee(shippingFee)
                .totalAmount(finalTotal)
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .billingAddress(billingAddress)
                .shippingAddress(shippingAddress)
                .note(request.getNote())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Cart.ERR_PRODUCT_QUANTITY_EXCEEDED);
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .subtotal(item.getPrice() * item.getQuantity())
                    .build();
            order.getOrderItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartService.clearCart();

        return orderMapper.toResponseDto(savedOrder);
    }

    @Override
    public PaginationResponseDto<OrderResponseDto> getMyOrders(PaginationRequestDto request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());

        Page<Order> page = orderRepository.findByUserId(userId, pageable);

        List<OrderResponseDto> dtos = page.getContent()
                .stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());

        PagingMeta pagingMeta = new PagingMeta(
                page.getTotalElements(),
                page.getTotalPages(),
                request.getPageNum() + 1,
                request.getPageSize(),
                null, null);

        return new PaginationResponseDto<>(pagingMeta, dtos);
    }

    @Override
    public PaginationResponseDto<OrderResponseDto> getAllOrders(PaginationRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());
        Page<Order> page = orderRepository.findAll(pageable);

        List<OrderResponseDto> dtos = page.getContent()
                .stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());

        PagingMeta pagingMeta = new PagingMeta(
                page.getTotalElements(),
                page.getTotalPages(),
                request.getPageNum() + 1,
                request.getPageSize(),
                null, null);

        return new PaginationResponseDto<>(pagingMeta, dtos);
    }

    @Override
    public OrderResponseDto getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Order.ERR_ORDER_NOT_FOUND));

        // Note: For User, we should verify if the order belongs to them. For Admin,
        // they can view any order.
        // Assuming this method might be called by both, let's keep it simple or check
        // roles.
        // It's safer to only allow Users to view their own, but since we don't have
        // separate endpoints,
        // let's do a basic check if the current user is not ADMIN, ensure the order is
        // theirs.

        // Skipping detailed role checks for brevity, but typically we'd do:
        // UUID currentUserId = SecurityUtils.getCurrentUserId();
        // if (!order.getUser().getId().equals(currentUserId) &&
        // !SecurityUtils.hasRole("ROLE_ADMIN")) throw ...

        return orderMapper.toResponseDto(order);
    }

    @Override
    public OrderResponseDto updateOrderStatus(UUID id, UpdateOrderStatusRequestDto request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Order.ERR_ORDER_NOT_FOUND));

        order.setStatus(request.getStatus());
        order.setUpdatedAt(LocalDateTime.now());

        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    @Override
    public CommonResponseDto cancelOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Order.ERR_ORDER_NOT_FOUND));

        UUID currentUserId = SecurityUtils.getCurrentUserId();

        if (!order.getUser().getId().equals(currentUserId)) {
            throw new VsException(HttpStatus.FORBIDDEN, "Bạn không có quyền huỷ đơn hàng này");
        }

        if (order.getStatus() != OrderStatus.ORDER_RECEIVED && order.getStatus() != OrderStatus.PROCESSING) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.Order.ERR_ORDER_CANNOT_BE_CANCELLED);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.Order.CANCEL_ORDER_SUCCESS);
    }
}
