package com.livehealth.order;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.order.dto.request.order.CreateOrderRequestDto;
import com.livehealth.order.dto.request.order.UpdateOrderStatusRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.order.dto.response.order.OrderResponseDto;

import java.util.UUID;

public interface OrderService {

    OrderResponseDto placeOrder(CreateOrderRequestDto request);

    PaginationResponseDto<OrderResponseDto> getMyOrders(PaginationRequestDto request);

    PaginationResponseDto<OrderResponseDto> getAllOrders(PaginationRequestDto request);

    OrderResponseDto getOrderById(UUID id);

    OrderResponseDto updateOrderStatus(UUID id, UpdateOrderStatusRequestDto request);

    CommonResponseDto cancelOrder(UUID id);
}
