package com.livehealth.news;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.news.dto.request.news.CreateNewsRequestDto;
import com.livehealth.news.dto.request.news.UpdateNewsRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.news.dto.response.news.NewsResponseDto;
import com.livehealth.shared.base.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface NewsService {

    PaginationResponseDto<NewsResponseDto> getAllNews(UUID categoryId, UUID tagId, PaginationRequestDto request);

    NewsResponseDto getNewsById(UUID id);

    NewsResponseDto createNews(CreateNewsRequestDto request);

    NewsResponseDto updateNews(UUID id, UpdateNewsRequestDto request);

    NewsResponseDto uploadThumbnail(UUID id, MultipartFile file);

    NewsResponseDto uploadImages(UUID id, List<MultipartFile> files);

    CommonResponseDto deleteNews(UUID id);

    NewsResponseDto addTagsToNews(UUID id, List<UUID> tagIds);

    NewsResponseDto removeTagFromNews(UUID id, UUID tagId);
}
