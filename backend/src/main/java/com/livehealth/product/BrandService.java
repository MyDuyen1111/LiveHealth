package com.livehealth.product;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.product.dto.request.brand.CreateBrandRequestDto;
import com.livehealth.product.dto.request.brand.UpdateBrandRequestDto;
import com.livehealth.product.dto.response.brand.BrandResponseDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.shared.base.MultipartFile;

import java.util.UUID;

public interface BrandService {

    // Public
    PaginationResponseDto<BrandResponseDto> getAllBrands(PaginationRequestDto request);

    BrandResponseDto getBrandById(UUID id);

    // Admin
    BrandResponseDto createBrand(CreateBrandRequestDto request);

    BrandResponseDto updateBrand(UUID id, UpdateBrandRequestDto request);

    BrandResponseDto uploadLogo(UUID id, MultipartFile file);

    CommonResponseDto deleteBrand(UUID id);
}
