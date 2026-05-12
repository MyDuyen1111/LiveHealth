package com.livehealth.news;

import com.livehealth.news.dto.request.tag.NewsTagRequestDto;
import com.livehealth.news.dto.response.tag.NewsTagResponseDto;
import com.livehealth.news.NewsTag;
import org.mapstruct.*;

@Mapper(componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface NewsTagMapper {

    NewsTagResponseDto toResponseDto(NewsTag tag);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "newsList", ignore = true)
    NewsTag toEntity(NewsTagRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "newsList", ignore = true)
    void updateEntityFromDto(NewsTagRequestDto request, @MappingTarget NewsTag tag);
}
