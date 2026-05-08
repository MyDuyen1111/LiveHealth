package com.livehealth.product.dto.request.promotion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreatePromotionRequestDto {

    @NotBlank(message = "Tên khuyến mãi không được để trống")
    String name;

    @NotNull(message = "Giá trị giảm giá không được để trống")
    Double discountValue;

    @NotBlank(message = "Loại giảm giá không được để trống")
    String discountType;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    LocalDateTime endDate;
}
