package com.livehealth.news;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.news.dto.request.news.CreateNewsRequestDto;
import com.livehealth.news.dto.request.news.UpdateNewsRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.news.dto.response.news.NewsResponseDto;
import com.livehealth.news.News;
import com.livehealth.news.NewsCategory;
import com.livehealth.news.NewsTag;
import com.livehealth.user.User;
import com.livehealth.news.NewsMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.news.NewsCategoryRepository;
import com.livehealth.news.NewsRepository;
import com.livehealth.news.NewsTagRepository;
import com.livehealth.user.UserRepository;
import com.livehealth.shared.security.SecurityUtils;
import com.livehealth.news.NewsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import com.livehealth.shared.base.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsServiceImpl implements NewsService {

    NewsRepository newsRepository;
    NewsCategoryRepository newsCategoryRepository;
    NewsTagRepository newsTagRepository;
    UserRepository userRepository;
    NewsMapper newsMapper;
    Cloudinary cloudinary;

    @Override
    public PaginationResponseDto<NewsResponseDto> getAllNews(UUID categoryId, UUID tagId, PaginationRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());

        Page<News> page = newsRepository.findNewsWithFilters(categoryId, tagId, pageable);

        List<NewsResponseDto> dtos = page.getContent()
                .stream()
                .map(newsMapper::toResponseDto)
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
    public NewsResponseDto getNewsById(UUID id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.News.ERR_NEWS_NOT_FOUND));

        return newsMapper.toResponseDto(news);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public NewsResponseDto createNews(CreateNewsRequestDto request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.User.ERR_USER_NOT_EXISTED));

        NewsCategory category = null;
        if (request.getCategoryId() != null) {
            category = newsCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.NewsCategory.ERR_CATEGORY_NOT_FOUND));
        }

        List<NewsTag> tags = new ArrayList<>();
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            tags = newsTagRepository.findAllById(request.getTagIds());
        }

        News news = newsMapper.toEntity(request);
        news.setAuthor(author);
        news.setCategory(category);
        news.setTags(tags);
        news.setCreatedAt(LocalDateTime.now());
        news.setUpdatedAt(LocalDateTime.now());

        return newsMapper.toResponseDto(newsRepository.save(news));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public NewsResponseDto updateNews(UUID id, UpdateNewsRequestDto request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.News.ERR_NEWS_NOT_FOUND));

        if (request.getCategoryId() != null) {
            NewsCategory category = newsCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.NewsCategory.ERR_CATEGORY_NOT_FOUND));
            news.setCategory(category);
        }

        if (request.getTagIds() != null) {
            List<NewsTag> tags = newsTagRepository.findAllById(request.getTagIds());
            news.setTags(tags);
        }

        newsMapper.updateEntityFromDto(request, news);
        news.setUpdatedAt(LocalDateTime.now());

        return newsMapper.toResponseDto(newsRepository.save(news));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public NewsResponseDto uploadThumbnail(UUID id, MultipartFile file) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.News.ERR_NEWS_NOT_FOUND));

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            news.setThumbnailUrl((String) result.get("secure_url"));
        } catch (Exception e) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.ERR_UPLOAD_IMAGE_FAIL);
        }

        news.setUpdatedAt(LocalDateTime.now());
        return newsMapper.toResponseDto(newsRepository.save(news));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public NewsResponseDto uploadImages(UUID id, List<MultipartFile> files) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.News.ERR_NEWS_NOT_FOUND));

        try {
            if (files.size() > 0 && files.get(0) != null) {
                Map<?, ?> result1 = cloudinary.uploader().upload(files.get(0).getBytes(), ObjectUtils.emptyMap());
                news.setImageUrl1((String) result1.get("secure_url"));
            }
            if (files.size() > 1 && files.get(1) != null) {
                Map<?, ?> result2 = cloudinary.uploader().upload(files.get(1).getBytes(), ObjectUtils.emptyMap());
                news.setImageUrl2((String) result2.get("secure_url"));
            }
        } catch (Exception e) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.ERR_UPLOAD_IMAGE_FAIL);
        }

        news.setUpdatedAt(LocalDateTime.now());
        return newsMapper.toResponseDto(newsRepository.save(news));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResponseDto deleteNews(UUID id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.News.ERR_NEWS_NOT_FOUND));

        newsRepository.delete(news);
        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.News.DELETE_NEWS_SUCCESS);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public NewsResponseDto addTagsToNews(UUID id, List<UUID> tagIds) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.News.ERR_NEWS_NOT_FOUND));

        List<NewsTag> existingTags = news.getTags();
        List<NewsTag> newTags = newsTagRepository.findAllById(tagIds);

        for (NewsTag tag : newTags) {
            if (!existingTags.contains(tag)) {
                existingTags.add(tag);
            }
        }

        news.setTags(existingTags);
        news.setUpdatedAt(LocalDateTime.now());

        return newsMapper.toResponseDto(newsRepository.save(news));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public NewsResponseDto removeTagFromNews(UUID id, UUID tagId) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.News.ERR_NEWS_NOT_FOUND));

        NewsTag tagToRemove = newsTagRepository.findById(tagId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.NewsTag.ERR_TAG_NOT_FOUND));

        List<NewsTag> tags = news.getTags();
        tags.remove(tagToRemove);

        news.setTags(tags);
        news.setUpdatedAt(LocalDateTime.now());

        return newsMapper.toResponseDto(newsRepository.save(news));
    }
}
