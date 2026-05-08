package com.livehealth.product;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.product.dto.request.tag.CreateTagRequestDto;
import com.livehealth.product.dto.request.tag.UpdateTagRequestDto;
import com.livehealth.product.dto.response.tag.TagResponseDto;
import com.livehealth.shared.dto.CommonResponseDto;

import java.util.UUID;

public interface TagService {

    // Public
    PaginationResponseDto<TagResponseDto> getAllTags(PaginationRequestDto request);

    TagResponseDto getTagById(UUID id);

    // Admin
    TagResponseDto createTag(CreateTagRequestDto request);

    TagResponseDto updateTag(UUID id, UpdateTagRequestDto request);

    CommonResponseDto deleteTag(UUID id);
}
