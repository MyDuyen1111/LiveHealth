package com.livehealth.news;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.news.dto.request.comment.NewsCommentRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.news.dto.response.comment.NewsCommentResponseDto;

import java.util.UUID;

public interface NewsCommentService {

    PaginationResponseDto<NewsCommentResponseDto> getCommentsByNewsId(UUID newsId, PaginationRequestDto request);

    NewsCommentResponseDto getCommentById(UUID id);

    NewsCommentResponseDto createComment(UUID newsId, NewsCommentRequestDto request);

    NewsCommentResponseDto updateComment(UUID id, NewsCommentRequestDto request);

    CommonResponseDto deleteComment(UUID id);
}
