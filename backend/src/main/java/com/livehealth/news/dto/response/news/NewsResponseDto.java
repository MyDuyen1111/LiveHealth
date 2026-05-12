package com.livehealth.news.dto.response.news;

import com.livehealth.news.dto.response.category.NewsCategoryResponseDto;
import com.livehealth.news.dto.response.tag.NewsTagResponseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsResponseDto {

    UUID id;

    String title;

    String content;

    String thumbnailUrl;

    String imageUrl1;

    String imageUrl2;

    UUID authorId;

    String authorName;

    NewsCategoryResponseDto category;

    List<NewsTagResponseDto> tags;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
