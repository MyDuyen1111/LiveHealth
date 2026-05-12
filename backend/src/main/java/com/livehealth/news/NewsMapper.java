package com.livehealth.news;

import com.livehealth.news.dto.request.news.CreateNewsRequestDto;
import com.livehealth.news.dto.request.news.UpdateNewsRequestDto;
import com.livehealth.news.dto.response.news.NewsResponseDto;
import com.livehealth.news.News;
import org.mapstruct.*;

@Mapper(componentModel = "cdi",
    uses = {NewsCategoryMapper.class, NewsTagMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface NewsMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.firstName")
    NewsResponseDto toResponseDto(News news);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "imageUrl1", ignore = true)
    @Mapping(target = "imageUrl2", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    News toEntity(CreateNewsRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "imageUrl1", ignore = true)
    @Mapping(target = "imageUrl2", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateNewsRequestDto request, @MappingTarget News news);
}
