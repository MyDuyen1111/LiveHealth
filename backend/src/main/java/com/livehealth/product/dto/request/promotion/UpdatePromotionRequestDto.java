package com.livehealth.product.dto.request.promotion;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePromotionRequestDto {

    String name;

    Double discountValue;

    String discountType;

    LocalDateTime startDate;

    LocalDateTime endDate;

    Boolean isActive;
}
