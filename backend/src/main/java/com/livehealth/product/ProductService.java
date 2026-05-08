package com.livehealth.product;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.product.dto.request.product.CreateProductRequestDto;
import com.livehealth.product.dto.request.product.UpdateProductRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.product.ProductResponseDto;
import com.livehealth.shared.base.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {

  PaginationResponseDto<ProductResponseDto> getAllProducts(PaginationRequestDto request);

  ProductResponseDto getProductById(UUID id);

  ProductResponseDto createProduct(CreateProductRequestDto request);

  ProductResponseDto updateProduct(UUID id, UpdateProductRequestDto request);

  ProductResponseDto uploadImages(UUID id, List<MultipartFile> files);

  CommonResponseDto deleteProduct(UUID id);

  ProductResponseDto addTagsToProduct(UUID id, List<UUID> tagIds);

  ProductResponseDto removeTagFromProduct(UUID id, UUID tagId);
}
