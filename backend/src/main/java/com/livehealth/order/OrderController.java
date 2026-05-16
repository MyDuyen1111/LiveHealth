package com.livehealth.order;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.order.dto.request.order.CreateOrderRequestDto;
import com.livehealth.order.dto.request.order.UpdateOrderStatusRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.order.dto.response.order.OrderResponseDto;
import com.livehealth.order.OrderService;
import com.livehealth.shared.base.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    // ==================== USER ====================

    @Operation(summary = "Lấy danh sách đơn hàng của tôi", description = "Lấy tất cả đơn hàng của tài khoản đang đăng nhập", security = @SecurityRequirement(name = "Bearer Token"))
    @GET
    @Path(UrlConstant.Order.GET_MY_ORDERS)
    public Response getMyOrders(@BeanParam PaginationRequestDto request) {
        PaginationResponseDto<OrderResponseDto> response = orderService.getMyOrders(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Đặt hàng", description = "Tạo đơn hàng mới từ giỏ hàng hiện tại", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.Order.PLACE_ORDER)
    public Response placeOrder(@Valid CreateOrderRequestDto request) {
        OrderResponseDto response = orderService.placeOrder(request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Huỷ đơn hàng", description = "Huỷ đơn hàng (chỉ áp dụng nếu đơn hàng đang ở trạng thái mới hoặc xử lý)", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.Order.CANCEL_ORDER)
    public Response cancelOrder(@PathParam("id") UUID id) {
        CommonResponseDto response = orderService.cancelOrder(id);
        return VsResponseUtil.success(response);
    }

    // ==================== BOTH (USER/ADMIN) ====================

    @Operation(summary = "Xem chi tiết đơn hàng", description = "Lấy thông tin chi tiết một đơn hàng theo ID", security = @SecurityRequirement(name = "Bearer Token"))
    @GET
    @Path(UrlConstant.Order.GET_ORDER_BY_ID)
    public Response getOrderById(@PathParam("id") UUID id) {
        OrderResponseDto response = orderService.getOrderById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Lấy danh sách tất cả đơn hàng", description = "Dành cho Admin xem tất cả các đơn hàng", security = @SecurityRequirement(name = "Bearer Token"))
    @GET
    @Path(UrlConstant.Order.GET_ALL_ORDERS)
    public Response getAllOrders(@BeanParam PaginationRequestDto request) {
        PaginationResponseDto<OrderResponseDto> response = orderService.getAllOrders(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Cập nhật trạng thái đơn hàng", description = "Admin cập nhật trạng thái đơn hàng (ví dụ: đang giao, hoàn thành)", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.Order.UPDATE_ORDER_STATUS)
    public Response updateOrderStatus(
            @PathParam("id") UUID id,
            @Valid UpdateOrderStatusRequestDto request) {
        OrderResponseDto response = orderService.updateOrderStatus(id, request);
        return VsResponseUtil.success(response);
    }
}
