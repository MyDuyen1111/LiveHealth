package com.livehealth.product;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.product.dto.request.attribute.CreateAttributeRequestDto;
import com.livehealth.product.dto.request.attribute.UpdateAttributeRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.attribute.AttributeResponseDto;
import com.livehealth.product.ProductAttributeService;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductAttributeController {

    ProductAttributeService attributeService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách thuộc tính của sản phẩm", description = "Tìm các thuộc tính (ví dụ Kích thước, Màu sắc, Chất liệu) của sản phẩm dựa theo ID sản phẩm")
    @GET
    @Path("/products/{productId}/attributes")
    public Response getAttributesByProductId(
            @PathParam("productId") UUID productId) {
        List<AttributeResponseDto> response = attributeService.getAttributesByProductId(productId);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết thuộc tính", description = "Lấy thuộc tính dựa theo ID thuộc tính")
    @GET
    @Path("/product-attributes/{id}")
    public Response getAttributeById(@PathParam("id") UUID id) {
        AttributeResponseDto response = attributeService.getAttributeById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Tạo thuộc tính mới", description = "Admin thêm thuộc tính (Key-Value) cho sản phẩm", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path("/products/{productId}/attributes")
    public Response createAttribute(
            @PathParam("productId") UUID productId,
            @Valid CreateAttributeRequestDto request) {
        AttributeResponseDto response = attributeService.createAttribute(productId, request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật thuộc tính", description = "Admin cập nhật thuộc tính", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path("/product-attributes/{id}")
    public Response updateAttribute(
            @PathParam("id") UUID id,
            @Valid UpdateAttributeRequestDto request) {
        AttributeResponseDto response = attributeService.updateAttribute(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa thuộc tính", description = "Admin xóa thuộc tính của sản phẩm", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path("/product-attributes/{id}")
    public Response deleteAttribute(@PathParam("id") UUID id) {
        CommonResponseDto response = attributeService.deleteAttribute(id);
        return VsResponseUtil.success(response);
    }
}
