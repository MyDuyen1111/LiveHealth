package com.livehealth.product;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.product.dto.request.product.CreateProductRequestDto;
import com.livehealth.product.dto.request.product.UpdateProductRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.product.ProductResponseDto;
import com.livehealth.product.ProductService;
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
import com.livehealth.shared.base.MultipartFile;
import com.livehealth.shared.base.QuarkusMultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestApiV1
@Path("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

  ProductService productService;

  // ==================== PUBLIC ====================

  @Operation(summary = "Lấy danh sách sản phẩm", description = "Xem danh sách sản phẩm có phân trang")
  @GET
  public Response getAllProducts(
      @BeanParam PaginationRequestDto request) {
    PaginationResponseDto<ProductResponseDto> response = productService.getAllProducts(request);
    return VsResponseUtil.success(response);
  }

  @Operation(summary = "Lấy chi tiết sản phẩm", description = "Tìm thông tin chi tiết của 1 sản phẩm")
  @GET
  @Path("/{id}")
  public Response getProductById(@PathParam("id") UUID id) {
    ProductResponseDto response = productService.getProductById(id);
    return VsResponseUtil.success(response);
  }

  // ==================== ADMIN ====================

  @Operation(summary = "Tạo sản phẩm mới", description = "Admin tạo mới sản phẩm", security = @SecurityRequirement(name = "Bearer Token"))
  @POST
  public Response createProduct(
      @Valid CreateProductRequestDto request) {
    ProductResponseDto response = productService.createProduct(request);
    return VsResponseUtil.success(HttpStatus.CREATED, response);
  }

  @Operation(summary = "Tải lên ảnh sản phẩm", description = "Admin tải lên nhiều ảnh cho sản phẩm", security = @SecurityRequirement(name = "Bearer Token"))
  @POST
  @Path("/{id}/images")
  public Response uploadImages(
      @PathParam("id") UUID id,
      @RestForm("file") List<FileUpload> files) {
    List<MultipartFile> multipartFiles = files.stream()
        .map(QuarkusMultipartFile::new)
        .collect(Collectors.toList());
    ProductResponseDto response = productService.uploadImages(id, multipartFiles);
    return VsResponseUtil.success(response);
  }

  @Operation(summary = "Cập nhật sản phẩm", description = "Admin cập nhật thông tin sản phẩm", security = @SecurityRequirement(name = "Bearer Token"))
  @PUT
  @Path("/{id}")
  public Response updateProduct(
      @PathParam("id") UUID id,
      @Valid UpdateProductRequestDto request) {
    ProductResponseDto response = productService.updateProduct(id, request);
    return VsResponseUtil.success(response);
  }

  @Operation(summary = "Xóa sản phẩm", description = "Admin xóa sản phẩm", security = @SecurityRequirement(name = "Bearer Token"))
  @DELETE
  @Path("/{id}")
  public Response deleteProduct(@PathParam("id") UUID id) {
    CommonResponseDto response = productService.deleteProduct(id);
    return VsResponseUtil.success(response);
  }

  @Operation(summary = "Thêm tags vào sản phẩm", description = "Admin gán tags cho sản phẩm", security = @SecurityRequirement(name = "Bearer Token"))
  @POST
  @Path("/{id}/tags")
  public Response addTagsToProduct(
      @PathParam("id") UUID id,
      List<UUID> tagIds) {
    ProductResponseDto response = productService.addTagsToProduct(id, tagIds);
    return VsResponseUtil.success(response);
  }

  @Operation(summary = "Xóa tag khỏi sản phẩm", description = "Admin gỡ tag khỏi sản phẩm", security = @SecurityRequirement(name = "Bearer Token"))
  @DELETE
  @Path("/{id}/tags/{tagId}")
  public Response removeTagFromProduct(
      @PathParam("id") UUID id,
      @PathParam("tagId") UUID tagId) {
    ProductResponseDto response = productService.removeTagFromProduct(id, tagId);
    return VsResponseUtil.success(response);
  }
}
