package com.livehealth.news;

import com.livehealth.news.dto.request.category.NewsCategoryRequestDto;
import com.livehealth.news.dto.response.category.NewsCategoryResponseDto;
import com.livehealth.news.NewsCategory;
import org.mapstruct.*;

@Mapper(componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface NewsCategoryMapper {

    NewsCategoryResponseDto toResponseDto(NewsCategory category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "newsList", ignore = true)
    NewsCategory toEntity(NewsCategoryRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "newsList", ignore = true)
    void updateEntityFromDto(NewsCategoryRequestDto request, @MappingTarget NewsCategory category);
}
