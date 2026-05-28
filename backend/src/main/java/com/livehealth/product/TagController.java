package com.livehealth.product;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.product.dto.request.tag.CreateTagRequestDto;
import com.livehealth.product.dto.request.tag.UpdateTagRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.tag.TagResponseDto;
import com.livehealth.product.TagService;
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
@Path("/tags")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TagController {

    TagService tagService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách tất cả tag", description = "Dùng để lấy danh sách tất cả tag sản phẩm có phân trang")
    @GET
    public Response getAllTags(
            @BeanParam PaginationRequestDto request) {
        PaginationResponseDto<TagResponseDto> response = tagService.getAllTags(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết tag theo ID", description = "Dùng để lấy thông tin chi tiết của một tag")
    @GET
    @Path("/{id}")
    public Response getTagById(@PathParam("id") UUID id) {
        TagResponseDto response = tagService.getTagById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Tạo tag mới", description = "Dùng để admin tạo tag sản phẩm mới", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    public Response createTag(
            @Valid CreateTagRequestDto request) {
        TagResponseDto response = tagService.createTag(request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật tag", description = "Dùng để admin cập nhật thông tin tag sản phẩm", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path("/{id}")
    public Response updateTag(
            @PathParam("id") UUID id,
            @Valid UpdateTagRequestDto request) {
        TagResponseDto response = tagService.updateTag(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa tag", description = "Dùng để admin xóa tag sản phẩm", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path("/{id}")
    public Response deleteTag(@PathParam("id") UUID id) {
        CommonResponseDto response = tagService.deleteTag(id);
        return VsResponseUtil.success(response);
    }
}
