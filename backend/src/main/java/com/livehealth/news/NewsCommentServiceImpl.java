package com.livehealth.news;

import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.news.dto.request.comment.NewsCommentRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.news.dto.response.comment.NewsCommentResponseDto;
import com.livehealth.news.News;
import com.livehealth.news.NewsComment;
import com.livehealth.user.User;
import com.livehealth.news.NewsCommentMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.news.NewsCommentRepository;
import com.livehealth.news.NewsRepository;
import com.livehealth.user.UserRepository;
import com.livehealth.shared.security.SecurityUtils;
import com.livehealth.news.NewsCommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsCommentServiceImpl implements NewsCommentService {

    NewsCommentRepository newsCommentRepository;
    NewsRepository newsRepository;
    UserRepository userRepository;
    NewsCommentMapper newsCommentMapper;

    @Override
    public PaginationResponseDto<NewsCommentResponseDto> getCommentsByNewsId(UUID newsId, PaginationRequestDto request) {
        if (!newsRepository.existsById(newsId)) {
            throw new VsException(HttpStatus.NOT_FOUND, ErrorMessage.News.ERR_NEWS_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());
        Page<NewsComment> page = newsCommentRepository.findByNewsId(newsId, pageable);

        List<NewsCommentResponseDto> dtos = page.getContent()
                .stream()
                .map(newsCommentMapper::toResponseDto)
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
    public NewsCommentResponseDto getCommentById(UUID id) {
        NewsComment comment = newsCommentRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.NewsComment.ERR_COMMENT_NOT_FOUND));

        return newsCommentMapper.toResponseDto(comment);
    }

    @Override
    public NewsCommentResponseDto createComment(UUID newsId, NewsCommentRequestDto request) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.News.ERR_NEWS_NOT_FOUND));

        UUID userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.User.ERR_USER_NOT_EXISTED));

        NewsComment comment = newsCommentMapper.toEntity(request);
        comment.setNews(news);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        return newsCommentMapper.toResponseDto(newsCommentRepository.save(comment));
    }

    @Override
    public NewsCommentResponseDto updateComment(UUID id, NewsCommentRequestDto request) {
        NewsComment comment = newsCommentRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.NewsComment.ERR_COMMENT_NOT_FOUND));

        UUID userId = SecurityUtils.getCurrentUserId();
        if (!comment.getUser().getId().equals(userId)) {
            throw new VsException(HttpStatus.FORBIDDEN, "Bạn không có quyền sửa bình luận này");
        }

        newsCommentMapper.updateEntityFromDto(request, comment);
        return newsCommentMapper.toResponseDto(newsCommentRepository.save(comment));
    }

    @Override
    public CommonResponseDto deleteComment(UUID id) {
        NewsComment comment = newsCommentRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.NewsComment.ERR_COMMENT_NOT_FOUND));

        newsCommentRepository.delete(comment);
        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.NewsComment.DELETE_COMMENT_SUCCESS);
    }
}
