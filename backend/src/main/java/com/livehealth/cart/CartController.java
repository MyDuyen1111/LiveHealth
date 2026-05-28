package com.livehealth.cart;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.cart.dto.request.cart.CartItemRequestDto;
import com.livehealth.cart.dto.request.cart.UpdateCartItemRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.cart.dto.response.cart.CartResponseDto;
import com.livehealth.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.HttpStatus;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.UUID;

@RestApiV1
@Path("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    // ==================== USER ====================

    @Operation(summary = "Lấy giỏ hàng của tôi", description = "Lấy thông tin chi tiết giỏ hàng của user đang đăng nhập", security = @SecurityRequirement(name = "Bearer Token"))
    @GET
    @Path(UrlConstant.Cart.GET_MY_CART)
    public Response getMyCart() {
        CartResponseDto response = cartService.getMyCart();
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Thêm sản phẩm vào giỏ hàng", description = "Bỏ sản phẩm vào giỏ", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.Cart.ADD_ITEM)
    public Response addItem(@Valid CartItemRequestDto request) {
        CartResponseDto response = cartService.addItem(request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật số lượng sản phẩm", description = "Thay đổi số lượng của item trong giỏ hàng", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.Cart.UPDATE_ITEM)
    public Response updateItem(
            @PathParam("itemId") UUID itemId,
            @Valid UpdateCartItemRequestDto request) {
        CartResponseDto response = cartService.updateItem(itemId, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa sản phẩm khỏi giỏ hàng", description = "Xóa 1 item khỏi giỏ", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path(UrlConstant.Cart.REMOVE_ITEM)
    public Response removeItem(@PathParam("itemId") UUID itemId) {
        CommonResponseDto response = cartService.removeItem(itemId);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Làm rỗng giỏ hàng", description = "Xóa tất cả các item trong giỏ", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path(UrlConstant.Cart.CLEAR_CART)
    public Response clearCart() {
        CommonResponseDto response = cartService.clearCart();
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Áp dụng mã giảm giá", description = "Chọn mã giảm giá cho giỏ hàng", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.Cart.APPLY_PROMOTION)
    public Response applyPromotion(@PathParam("promotionId") UUID promotionId) {
        CartResponseDto response = cartService.applyPromotion(promotionId);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Chọn phương thức giao hàng", description = "Chọn phương thức giao hàng cho giỏ hàng", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.Cart.SELECT_SHIPPING_METHOD)
    public Response selectShippingMethod(@PathParam("shippingMethodId") UUID shippingMethodId) {
        CartResponseDto response = cartService.selectShippingMethod(shippingMethodId);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Chọn phương thức thanh toán", description = "Chọn phương thức thanh toán cho giỏ hàng", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.Cart.SELECT_PAYMENT_METHOD)
    public Response selectPaymentMethod(@PathParam("paymentMethodId") UUID paymentMethodId) {
        CartResponseDto response = cartService.selectPaymentMethod(paymentMethodId);
        return VsResponseUtil.success(response);
    }
}
