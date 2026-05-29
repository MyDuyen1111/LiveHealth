package com.livehealth.product;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.product.dto.request.brand.CreateBrandRequestDto;
import com.livehealth.product.dto.request.brand.UpdateBrandRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.brand.BrandResponseDto;
import com.livehealth.product.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.HttpStatus;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import com.livehealth.shared.base.QuarkusMultipartFile;

import java.util.UUID;

@RestApiV1
@Path("/api/v1/brands")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandController {

    BrandService brandService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách tất cả thương hiệu", description = "Dùng để lấy danh sách tất cả thương hiệu sản phẩm có phân trang")
    @GET
    public Response getAllBrands(
            @BeanParam PaginationRequestDto request) {
        PaginationResponseDto<BrandResponseDto> response = brandService.getAllBrands(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết thương hiệu theo ID", description = "Dùng để lấy thông tin chi tiết của một thương hiệu")
    @GET
    @Path("/{id}")
    public Response getBrandById(@PathParam("id") UUID id) {
        BrandResponseDto response = brandService.getBrandById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Tạo thương hiệu mới", description = "Dùng để admin tạo thương hiệu sản phẩm mới", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    public Response createBrand(
            @Valid CreateBrandRequestDto request) {
        BrandResponseDto response = brandService.createBrand(request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Tải lên logo thương hiệu", description = "Dùng để admin tải lên logo cho thương hiệu", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path("/{id}/logo")
    @Consumes(jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA)
    public Response uploadBrandLogo(
            @PathParam("id") UUID id,
            @RestForm("file") FileUpload file) {
        BrandResponseDto response = brandService.uploadLogo(id, new QuarkusMultipartFile(file));
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Cập nhật thương hiệu", description = "Dùng để admin cập nhật thông tin thương hiệu sản phẩm", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path("/{id}")
    public Response updateBrand(
            @PathParam("id") UUID id,
            @Valid UpdateBrandRequestDto request) {
        BrandResponseDto response = brandService.updateBrand(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa thương hiệu", description = "Dùng để admin xóa thương hiệu sản phẩm (chỉ xóa được khi không còn sản phẩm nào)", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path("/{id}")
    public Response deleteBrand(@PathParam("id") UUID id) {
        CommonResponseDto response = brandService.deleteBrand(id);
        return VsResponseUtil.success(response);
    }
}
