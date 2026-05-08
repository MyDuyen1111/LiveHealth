package com.livehealth.product.dto.response.review;

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
public class ReviewResponseDto {

    UUID id;

    int rating;

    String comment;

    UUID productId;

    UUID userId;

    String userFullName;

    LocalDateTime createdAt;
}
