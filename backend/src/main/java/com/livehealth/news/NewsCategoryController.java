package com.livehealth.news;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.news.dto.request.category.NewsCategoryRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.news.dto.response.category.NewsCategoryCountDto;
import com.livehealth.news.dto.response.category.NewsCategoryResponseDto;
import com.livehealth.news.NewsCategoryService;
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
public class NewsCategoryController {

    NewsCategoryService newsCategoryService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách các danh mục tin tức", description = "Lấy danh sách có phân trang")
    @GET
    @Path(UrlConstant.NewsCategory.GET_ALL_CATEGORIES)
    public Response getAllCategories(
            @BeanParam PaginationRequestDto request) {
        PaginationResponseDto<NewsCategoryResponseDto> response = newsCategoryService.getAllCategories(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết danh mục", description = "Tìm danh mục theo ID")
    @GET
    @Path(UrlConstant.NewsCategory.GET_CATEGORY_BY_ID)
    public Response getCategoryById(@PathParam("id") UUID id) {
        NewsCategoryResponseDto response = newsCategoryService.getCategoryById(id);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Đếm bài viết theo danh mục", description = "Thống kê số lượng bài viết của từng danh mục và sắp xếp giảm dần theo số lượng")
    @GET
    @Path(UrlConstant.NewsCategory.GET_CATEGORY_COUNTS)
    public Response getCategoryCounts() {
        List<NewsCategoryCountDto> response = newsCategoryService.getCategoryCounts();
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Tạo danh mục tin tức", description = "Admin tạo mới một danh mục", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.NewsCategory.CREATE_CATEGORY)
    public Response createCategory(
            @Valid NewsCategoryRequestDto request) {
        NewsCategoryResponseDto response = newsCategoryService.createCategory(request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật danh mục tin tức", description = "Cập nhật thông tin của một danh mục", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.NewsCategory.UPDATE_CATEGORY)
    public Response updateCategory(
            @PathParam("id") UUID id,
            @Valid NewsCategoryRequestDto request) {
        NewsCategoryResponseDto response = newsCategoryService.updateCategory(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa danh mục tin tức", description = "Xóa danh mục tin tức khỏi hệ thống", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path(UrlConstant.NewsCategory.DELETE_CATEGORY)
    public Response deleteCategory(@PathParam("id") UUID id) {
        CommonResponseDto response = newsCategoryService.deleteCategory(id);
        return VsResponseUtil.success(response);
    }
}
