package com.livehealth.order;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.cart.dto.request.cart.ShippingMethodRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.cart.dto.response.cart.ShippingMethodResponseDto;
import com.livehealth.order.ShippingMethodService;
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
public class ShippingMethodController {

    ShippingMethodService shippingMethodService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách các phương thức giao hàng", description = "Lấy danh sách có phân trang")
    @GET
    @Path(UrlConstant.ShippingMethod.GET_ALL_SHIPPING_METHODS)
    public Response getAllShippingMethods(@BeanParam PaginationRequestDto request) {
        PaginationResponseDto<ShippingMethodResponseDto> response = shippingMethodService.getAllShippingMethods(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết phương thức giao hàng", description = "Tìm phương thức giao hàng theo ID")
    @GET
    @Path(UrlConstant.ShippingMethod.GET_SHIPPING_METHOD_BY_ID)
    public Response getShippingMethodById(@PathParam("id") UUID id) {
        ShippingMethodResponseDto response = shippingMethodService.getShippingMethodById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Tạo phương thức giao hàng", description = "Admin tạo mới một phương thức giao hàng", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.ShippingMethod.CREATE_SHIPPING_METHOD)
    public Response createShippingMethod(@Valid ShippingMethodRequestDto request) {
        ShippingMethodResponseDto response = shippingMethodService.createShippingMethod(request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật phương thức giao hàng", description = "Cập nhật thông tin của một phương thức giao hàng", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.ShippingMethod.UPDATE_SHIPPING_METHOD)
    public Response updateShippingMethod(
            @PathParam("id") UUID id,
            @Valid ShippingMethodRequestDto request) {
        ShippingMethodResponseDto response = shippingMethodService.updateShippingMethod(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa phương thức giao hàng", description = "Xóa phương thức giao hàng khỏi hệ thống", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path(UrlConstant.ShippingMethod.DELETE_SHIPPING_METHOD)
    public Response deleteShippingMethod(@PathParam("id") UUID id) {
        CommonResponseDto response = shippingMethodService.deleteShippingMethod(id);
        return VsResponseUtil.success(response);
    }
}
