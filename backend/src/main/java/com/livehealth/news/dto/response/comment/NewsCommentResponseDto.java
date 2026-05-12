package com.livehealth.news.dto.response.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsCommentResponseDto {

    UUID id;

    String content;

    UUID newsId;

    UUID userId;

    String userFullName;

    LocalDateTime createdAt;
}
