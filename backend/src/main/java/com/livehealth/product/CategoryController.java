package com.livehealth.product;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.product.dto.request.category.CreateCategoryRequestDto;
import com.livehealth.product.dto.request.category.UpdateCategoryRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.category.CategoryResponseDto;
import com.livehealth.product.CategoryService;
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
@Path("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách tất cả danh mục", description = "Dùng để lấy danh sách tất cả danh mục sản phẩm có phân trang")
    @GET
    public Response getAllCategories(
            @BeanParam PaginationRequestDto request) {
        PaginationResponseDto<CategoryResponseDto> response = categoryService.getAllCategories(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết danh mục theo ID", description = "Dùng để lấy thông tin chi tiết của một danh mục")
    @GET
    @Path("/{id}")
    public Response getCategoryById(@PathParam("id") UUID id) {
        CategoryResponseDto response = categoryService.getCategoryById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Tạo danh mục mới", description = "Dùng để admin tạo danh mục sản phẩm mới", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    public Response createCategory(
            @Valid CreateCategoryRequestDto request) {
        CategoryResponseDto response = categoryService.createCategory(request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật danh mục", description = "Dùng để admin cập nhật thông tin danh mục sản phẩm", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path("/{id}")
    public Response updateCategory(
            @PathParam("id") UUID id,
            @Valid UpdateCategoryRequestDto request) {
        CategoryResponseDto response = categoryService.updateCategory(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa danh mục", description = "Dùng để admin xóa danh mục sản phẩm (chỉ xóa được khi không còn sản phẩm nào)", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") UUID id) {
        CommonResponseDto response = categoryService.deleteCategory(id);
        return VsResponseUtil.success(response);
    }
}
