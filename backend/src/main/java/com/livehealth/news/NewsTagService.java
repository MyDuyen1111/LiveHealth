package com.livehealth.news;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.news.dto.request.tag.NewsTagRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.news.dto.response.tag.NewsTagResponseDto;

import java.util.UUID;

public interface NewsTagService {

    PaginationResponseDto<NewsTagResponseDto> getAllTags(PaginationRequestDto request);

    NewsTagResponseDto getTagById(UUID id);

    NewsTagResponseDto createTag(NewsTagRequestDto request);

    NewsTagResponseDto updateTag(UUID id, NewsTagRequestDto request);

    CommonResponseDto deleteTag(UUID id);
}
