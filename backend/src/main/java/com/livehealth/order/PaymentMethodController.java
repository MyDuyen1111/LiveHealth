package com.livehealth.order;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.cart.dto.request.cart.PaymentMethodRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.cart.dto.response.cart.PaymentMethodResponseDto;
import com.livehealth.order.PaymentMethodService;
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
@Path("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentMethodController {

    PaymentMethodService paymentMethodService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách các phương thức thanh toán", description = "Lấy danh sách có phân trang")
    @GET
    @Path(UrlConstant.PaymentMethod.GET_ALL_PAYMENT_METHODS)
    public Response getAllPaymentMethods(@BeanParam PaginationRequestDto request) {
        PaginationResponseDto<PaymentMethodResponseDto> response = paymentMethodService.getAllPaymentMethods(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết phương thức thanh toán", description = "Tìm phương thức thanh toán theo ID")
    @GET
    @Path(UrlConstant.PaymentMethod.GET_PAYMENT_METHOD_BY_ID)
    public Response getPaymentMethodById(@PathParam("id") UUID id) {
        PaymentMethodResponseDto response = paymentMethodService.getPaymentMethodById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Tạo phương thức thanh toán", description = "Admin tạo mới một phương thức thanh toán", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.PaymentMethod.CREATE_PAYMENT_METHOD)
    public Response createPaymentMethod(@Valid PaymentMethodRequestDto request) {
        PaymentMethodResponseDto response = paymentMethodService.createPaymentMethod(request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật phương thức thanh toán", description = "Cập nhật thông tin của một phương thức thanh toán", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.PaymentMethod.UPDATE_PAYMENT_METHOD)
    public Response updatePaymentMethod(
            @PathParam("id") UUID id,
            @Valid PaymentMethodRequestDto request) {
        PaymentMethodResponseDto response = paymentMethodService.updatePaymentMethod(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa phương thức thanh toán", description = "Xóa phương thức thanh toán khỏi hệ thống", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path(UrlConstant.PaymentMethod.DELETE_PAYMENT_METHOD)
    public Response deletePaymentMethod(@PathParam("id") UUID id) {
        CommonResponseDto response = paymentMethodService.deletePaymentMethod(id);
        return VsResponseUtil.success(response);
    }
}
