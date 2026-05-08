package com.livehealth.product.dto.response.promotion;

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
public class PromotionResponseDto {

    UUID id;

    String name;

    double discountValue;

    String discountType;

    LocalDateTime startDate;

    LocalDateTime endDate;

    boolean isActive;

    UUID productId;

    LocalDateTime createdAt;
}
