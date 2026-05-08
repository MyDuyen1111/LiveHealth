package com.livehealth.product;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.product.dto.request.promotion.CreatePromotionRequestDto;
import com.livehealth.product.dto.request.promotion.UpdatePromotionRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.promotion.PromotionResponseDto;
import com.livehealth.product.ProductPromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.HttpStatus;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@RestApiV1
@Path("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductPromotionController {

    ProductPromotionService promotionService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách khuyến mãi của sản phẩm", description = "Lấy các khuyến mãi đang áp dụng hoặc từng áp dụng cho sản phẩm")
    @GET
    @Path("/products/{productId}/promotions")
    public Response getPromotionsByProductId(
            @PathParam("productId") UUID productId) {
        List<PromotionResponseDto> response = promotionService.getPromotionsByProductId(productId);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết khuyến mãi", description = "Lấy chi tiết khuyến mãi bằng ID")
    @GET
    @Path("/product-promotions/{id}")
    public Response getPromotionById(@PathParam("id") UUID id) {
        PromotionResponseDto response = promotionService.getPromotionById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Tạo khuyến mãi mới", description = "Admin tạo khuyến mãi cho sản phẩm. (Chỉ 1 khuyến mãi Active tại 1 thời điểm)", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path("/products/{productId}/promotions")
    public Response createPromotion(
            @PathParam("productId") UUID productId,
            @Valid CreatePromotionRequestDto request) {
        PromotionResponseDto response = promotionService.createPromotion(productId, request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật khuyến mãi", description = "Cập nhật ngày giờ, giá trị, trạng thái active của khuyến mãi", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path("/product-promotions/{id}")
    public Response updatePromotion(
            @PathParam("id") UUID id,
            @Valid UpdatePromotionRequestDto request) {
        PromotionResponseDto response = promotionService.updatePromotion(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa khuyến mãi", description = "Admin chức năng xóa khuyến mãi", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path("/product-promotions/{id}")
    public Response deletePromotion(@PathParam("id") UUID id) {
        CommonResponseDto response = promotionService.deletePromotion(id);
        return VsResponseUtil.success(response);
    }
}
