package com.livehealth.news;

import com.livehealth.news.dto.request.comment.NewsCommentRequestDto;
import com.livehealth.news.dto.response.comment.NewsCommentResponseDto;
import com.livehealth.news.NewsComment;
import org.mapstruct.*;

@Mapper(componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface NewsCommentMapper {

    @Mapping(target = "newsId", source = "news.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", source = "user.firstName") // Assuming firstName/lastName or fullName, based on other mappers
    NewsCommentResponseDto toResponseDto(NewsComment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "news", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    NewsComment toEntity(NewsCommentRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "news", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(NewsCommentRequestDto request, @MappingTarget NewsComment comment);
}
