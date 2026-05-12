package com.livehealth.news;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.news.dto.request.tag.NewsTagRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.news.dto.response.tag.NewsTagResponseDto;
import com.livehealth.news.NewsTagService;
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
public class NewsTagController {

    NewsTagService newsTagService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách các tag tin tức", description = "Lấy danh sách có phân trang")
    @GET
    @Path(UrlConstant.NewsTag.GET_ALL_TAGS)
    public Response getAllTags(@BeanParam PaginationRequestDto request) {
        PaginationResponseDto<NewsTagResponseDto> response = newsTagService.getAllTags(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết tag", description = "Tìm tag tin tức theo ID")
    @GET
    @Path(UrlConstant.NewsTag.GET_TAG_BY_ID)
    public Response getTagById(@PathParam("id") UUID id) {
        NewsTagResponseDto response = newsTagService.getTagById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Tạo tag mới", description = "Admin tạo tag cho bài viết", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.NewsTag.CREATE_TAG)
    public Response createTag(@Valid NewsTagRequestDto request) {
        NewsTagResponseDto response = newsTagService.createTag(request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật tag", description = "Sửa thông tin tag", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.NewsTag.UPDATE_TAG)
    public Response updateTag(
            @PathParam("id") UUID id,
            @Valid NewsTagRequestDto request) {
        NewsTagResponseDto response = newsTagService.updateTag(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa tag", description = "Xóa thông tin tag", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path(UrlConstant.NewsTag.DELETE_TAG)
    public Response deleteTag(@PathParam("id") UUID id) {
        CommonResponseDto response = newsTagService.deleteTag(id);
        return VsResponseUtil.success(response);
    }
}
