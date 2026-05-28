package com.livehealth.health.dto;

import com.livehealth.user.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HealthAnalysisRequestDto {

    @NotNull(message = "Chiều cao không được để trống")
    @Min(value = 50, message = "Chiều cao tối thiểu 50cm")
    @Max(value = 300, message = "Chiều cao tối đa 300cm")
    Integer height; // cm

    @NotNull(message = "Cân nặng không được để trống")
    @Min(value = 10, message = "Cân nặng tối thiểu 10kg")
    @Max(value = 500, message = "Cân nặng tối đa 500kg")
    Integer weight; // kg

    @NotNull(message = "Tuổi không được để trống")
    @Min(value = 1, message = "Tuổi tối thiểu 1")
    @Max(value = 150, message = "Tuổi tối đa 150")
    Integer age;

    @NotNull(message = "Giới tính không được để trống")
    Gender gender;

    @NotNull(message = "Mức vận động không được để trống")
    ActivityLevel activityLevel;

    public enum ActivityLevel {
        SEDENTARY(1.2, "Ít vận động"),
        LIGHT(1.375, "Nhẹ nhàng"),
        MODERATE(1.55, "Vừa phải"),
        ACTIVE(1.725, "Năng động"),
        VERY_ACTIVE(1.9, "Rất năng động");

        @Getter
        private final double multiplier;
        @Getter
        private final String label;

        ActivityLevel(double multiplier, String label) {
            this.multiplier = multiplier;
            this.label = label;
        }
    }
}
