package com.livehealth.news;

import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.news.dto.request.tag.NewsTagRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.news.dto.response.tag.NewsTagResponseDto;
import com.livehealth.news.NewsTag;
import com.livehealth.news.NewsTagMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.news.NewsTagRepository;
import com.livehealth.news.NewsTagService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsTagServiceImpl implements NewsTagService {

    NewsTagRepository newsTagRepository;
    NewsTagMapper newsTagMapper;

    @Override
    public PaginationResponseDto<NewsTagResponseDto> getAllTags(PaginationRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());
        Page<NewsTag> page = newsTagRepository.findAll(pageable);

        List<NewsTagResponseDto> dtos = page.getContent()
                .stream()
                .map(newsTagMapper::toResponseDto)
                .collect(Collectors.toList());

        PagingMeta pagingMeta = new PagingMeta(
                page.getTotalElements(),
                page.getTotalPages(),
                request.getPageNum() + 1,
                request.getPageSize(),
                null,
                null);

        return new PaginationResponseDto<>(pagingMeta, dtos);
    }

    @Override
    public NewsTagResponseDto getTagById(UUID id) {
        NewsTag tag = newsTagRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.NewsTag.ERR_TAG_NOT_FOUND));

        return newsTagMapper.toResponseDto(tag);
    }

    @Override
    public NewsTagResponseDto createTag(NewsTagRequestDto request) {
        if (newsTagRepository.existsByName(request.getName())) {
            throw new VsException(HttpStatus.CONFLICT, ErrorMessage.NewsTag.ERR_NAME_EXISTED);
        }
        NewsTag tag = newsTagMapper.toEntity(request);
        return newsTagMapper.toResponseDto(newsTagRepository.save(tag));
    }

    @Override
    public NewsTagResponseDto updateTag(UUID id, NewsTagRequestDto request) {
        NewsTag tag = newsTagRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.NewsTag.ERR_TAG_NOT_FOUND));

        if (!tag.getName().equals(request.getName()) && newsTagRepository.existsByName(request.getName())) {
            throw new VsException(HttpStatus.CONFLICT, ErrorMessage.NewsTag.ERR_NAME_EXISTED);
        }
        newsTagMapper.updateEntityFromDto(request, tag);
        return newsTagMapper.toResponseDto(newsTagRepository.save(tag));
    }

    @Override
    public CommonResponseDto deleteTag(UUID id) {
        NewsTag tag = newsTagRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.NewsTag.ERR_TAG_NOT_FOUND));
        newsTagRepository.delete(tag);
        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.NewsTag.DELETE_TAG_SUCCESS);
    }
}
