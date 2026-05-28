package com.livehealth.product;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.product.dto.request.review.CreateReviewRequestDto;
import com.livehealth.product.dto.request.review.UpdateReviewRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.review.ReviewResponseDto;
import com.livehealth.product.ReviewService;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    ReviewService reviewService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách đánh giá của sản phẩm", description = "Tìm các đánh giá dựa theo ID sản phẩm")
    @GET
    @Path("/products/{productId}/reviews")
    public Response getReviewsByProductId(
            @PathParam("productId") UUID productId,
            @BeanParam PaginationRequestDto request) {
        PaginationResponseDto<ReviewResponseDto> response = reviewService.getReviewsByProductId(productId, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết đánh giá", description = "Lấy đánh giá dựa theo ID của đánh giá")
    @GET
    @Path("/reviews/{id}")
    public Response getReviewById(@PathParam("id") UUID id) {
        ReviewResponseDto response = reviewService.getReviewById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== USER / ADMIN ====================

    @Operation(summary = "Tạo đánh giá mới cho sản phẩm", description = "Người dùng đánh giá sản phẩm sau khi mua", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path("/products/{productId}/reviews")
    public Response createReview(
            @PathParam("productId") UUID productId,
            @Valid CreateReviewRequestDto request) {
        ReviewResponseDto response = reviewService.createReview(productId, request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật đánh giá", description = "Người dùng tự cập nhật đánh giá của mình", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path("/reviews/{id}")
    public Response updateReview(
            @PathParam("id") UUID id,
            @Valid UpdateReviewRequestDto request) {
        ReviewResponseDto response = reviewService.updateReview(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa đánh giá", description = "Admin hoặc User có thể xóa đánh giá", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path("/reviews/{id}")
    public Response deleteReview(@PathParam("id") UUID id) {
        CommonResponseDto response = reviewService.deleteReview(id);
        return VsResponseUtil.success(response);
    }
}
