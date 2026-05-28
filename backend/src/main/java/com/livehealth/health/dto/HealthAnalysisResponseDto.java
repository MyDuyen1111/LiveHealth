package com.livehealth.health.dto;

import com.livehealth.product.dto.response.product.ProductResponseDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HealthAnalysisResponseDto {

    // BMI
    double bmi;
    String bmiStatus;
    String bmiCategory; // underweight, normal, overweight, obese

    // BMR & TDEE
    double bmr;
    double tdee;
    int dailyCalories;

    // Macros recommendation (g/day)
    int protein;
    int carbs;
    int fat;

    // AI advice
    String advice;
    List<String> dietTips;

    // Recommended categories & products from DB
    List<String> recommendedCategories;
    List<ProductResponseDto> recommendedProducts;
}
