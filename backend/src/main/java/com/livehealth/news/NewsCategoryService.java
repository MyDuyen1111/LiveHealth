package com.livehealth.news;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.news.dto.request.category.NewsCategoryRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.news.dto.response.category.NewsCategoryCountDto;
import com.livehealth.news.dto.response.category.NewsCategoryResponseDto;

import java.util.List;
import java.util.UUID;

public interface NewsCategoryService {

    PaginationResponseDto<NewsCategoryResponseDto> getAllCategories(PaginationRequestDto request);

    NewsCategoryResponseDto getCategoryById(UUID id);

    List<NewsCategoryCountDto> getCategoryCounts();

    NewsCategoryResponseDto createCategory(NewsCategoryRequestDto request);

    NewsCategoryResponseDto updateCategory(UUID id, NewsCategoryRequestDto request);

    CommonResponseDto deleteCategory(UUID id);
}
