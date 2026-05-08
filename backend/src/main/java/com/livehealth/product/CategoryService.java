package com.livehealth.product;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.product.dto.request.category.CreateCategoryRequestDto;
import com.livehealth.product.dto.request.category.UpdateCategoryRequestDto;
import com.livehealth.product.dto.response.category.CategoryResponseDto;
import com.livehealth.shared.dto.CommonResponseDto;

import java.util.UUID;

public interface CategoryService {

    // Public
    PaginationResponseDto<CategoryResponseDto> getAllCategories(PaginationRequestDto request);

    CategoryResponseDto getCategoryById(UUID id);

    // Admin
    CategoryResponseDto createCategory(CreateCategoryRequestDto request);

    CategoryResponseDto updateCategory(UUID id, UpdateCategoryRequestDto request);

    CommonResponseDto deleteCategory(UUID id);
}
