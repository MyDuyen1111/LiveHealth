package com.livehealth.health;

import com.livehealth.health.dto.HealthAnalysisRequestDto;
import com.livehealth.health.dto.HealthAnalysisResponseDto;
import com.livehealth.product.Product;
import com.livehealth.product.ProductMapper;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.dto.response.product.ProductResponseDto;
import com.livehealth.user.Gender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HealthServiceImpl implements HealthService {

    ProductRepository productRepository;
    ProductMapper productMapper;

    // ═══════════════════════════════════════════
    //  Category mapping for product recommendations
    // ═══════════════════════════════════════════
    private static final Map<String, List<String>> BMI_CATEGORY_MAP = Map.of(
        "underweight", List.of("Thịt", "Trứng & Sữa", "Cá & Hải sản"),
        "normal",      List.of("Rau Củ", "Hoa Quả", "Cá & Hải sản", "Thịt", "Trứng & Sữa"),
        "overweight",  List.of("Rau Củ", "Cá & Hải sản", "Hoa Quả"),
        "obese",       List.of("Rau Củ", "Hoa Quả")
    );

    @Override
    public HealthAnalysisResponseDto analyze(HealthAnalysisRequestDto req) {
        // ── 1. BMI ──
        double heightM = req.getHeight() / 100.0;
        double bmi = Math.round((req.getWeight() / (heightM * heightM)) * 10.0) / 10.0;

        String bmiStatus;
        String bmiCategory;
        if (bmi < 18.5) {
            bmiStatus = "Thiếu cân";
            bmiCategory = "underweight";
        } else if (bmi < 25) {
            bmiStatus = "Bình thường";
            bmiCategory = "normal";
        } else if (bmi < 30) {
            bmiStatus = "Thừa cân";
            bmiCategory = "overweight";
        } else {
            bmiStatus = "Béo phì";
            bmiCategory = "obese";
        }

        // ── 2. BMR (Mifflin-St Jeor) ──
        double bmr;
        if (req.getGender() == Gender.MALE) {
            bmr = 10 * req.getWeight() + 6.25 * req.getHeight() - 5 * req.getAge() + 5;
        } else {
            bmr = 10 * req.getWeight() + 6.25 * req.getHeight() - 5 * req.getAge() - 161;
        }
        bmr = Math.round(bmr * 10.0) / 10.0;

        // ── 3. TDEE ──
        double tdee = Math.round(bmr * req.getActivityLevel().getMultiplier() * 10.0) / 10.0;
        int dailyCalories = (int) Math.round(tdee);

        // ── 4. Macros split ──
        int protein, carbs, fat;
        switch (bmiCategory) {
            case "underweight":
                // High protein, high carbs for weight gain
                protein = (int) (dailyCalories * 0.25 / 4); // 25% protein
                carbs   = (int) (dailyCalories * 0.50 / 4);  // 50% carbs
                fat     = (int) (dailyCalories * 0.25 / 9);  // 25% fat
                break;
            case "overweight":
                // Higher protein, lower carbs
                protein = (int) (dailyCalories * 0.30 / 4);
                carbs   = (int) (dailyCalories * 0.40 / 4);
                fat     = (int) (dailyCalories * 0.30 / 9);
                break;
            case "obese":
                // Very high protein, low carbs
                protein = (int) (dailyCalories * 0.35 / 4);
                carbs   = (int) (dailyCalories * 0.35 / 4);
                fat     = (int) (dailyCalories * 0.30 / 9);
                break;
            default: // normal
                protein = (int) (dailyCalories * 0.25 / 4);
                carbs   = (int) (dailyCalories * 0.50 / 4);
                fat     = (int) (dailyCalories * 0.25 / 9);
                break;
        }

        // ── 5. AI advice ──
        String advice = buildAdvice(bmiCategory, dailyCalories);
        List<String> dietTips = buildDietTips(bmiCategory);

        // ── 6. Product recommendations from DB ──
        List<String> recommendedCategories = BMI_CATEGORY_MAP.getOrDefault(bmiCategory, List.of());
        List<ProductResponseDto> recommendedProducts = getProductsByCategories(recommendedCategories, 10);

        return HealthAnalysisResponseDto.builder()
                .bmi(bmi)
                .bmiStatus(bmiStatus)
                .bmiCategory(bmiCategory)
                .bmr(bmr)
                .tdee(tdee)
                .dailyCalories(dailyCalories)
                .protein(protein)
                .carbs(carbs)
                .fat(fat)
                .advice(advice)
                .dietTips(dietTips)
                .recommendedCategories(recommendedCategories)
                .recommendedProducts(recommendedProducts)
                .build();
    }

    // ═══════════════════════════════════════════
    //  Private helpers
    // ═══════════════════════════════════════════

    private List<ProductResponseDto> getProductsByCategories(List<String> categoryNames, int limit) {
        List<Product> allProducts = productRepository.findAll();

        List<Product> matched = allProducts.stream()
                .filter(p -> p.getCategory() != null
                        && categoryNames.stream().anyMatch(cat -> p.getCategory().getName().contains(cat)))
                .collect(Collectors.toList());

        // Shuffle to give variety each time
        Collections.shuffle(matched);

        return matched.stream()
                .limit(limit)
                .map(productMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private String buildAdvice(String category, int calories) {
        return switch (category) {
            case "underweight" -> String.format(
                "Hệ thống AI nhận thấy bạn đang thiếu cân. Bạn cần nạp khoảng %d kcal/ngày. " +
                "Hãy tăng cường các loại thực phẩm giàu protein như thịt bò, cá hồi, trứng và sữa. " +
                "Chia thành 5-6 bữa nhỏ trong ngày để dễ hấp thu.", calories);
            case "normal" -> String.format(
                "Tuyệt vời! Thể trạng của bạn đang ở mức lý tưởng. Duy trì %d kcal/ngày. " +
                "Hãy ăn đa dạng và cân bằng giữa thịt, cá, rau củ và hoa quả để giữ sức khỏe tốt.", calories);
            case "overweight" -> String.format(
                "Bạn đang bị thừa cân nhẹ. Nên giảm xuống khoảng %d kcal/ngày. " +
                "Ưu tiên rau xanh, cá và hoa quả ít đường. Hạn chế tinh bột và đồ chiên rán.", calories - 300);
            case "obese" -> String.format(
                "Chỉ số BMI cho thấy bạn đang béo phì. Cần kiểm soát ở mức %d kcal/ngày. " +
                "Tăng cường rau xanh và hoa quả ít đường. Nên kết hợp tập thể dục đều đặn " +
                "và tham khảo ý kiến bác sĩ dinh dưỡng.", calories - 500);
            default -> "Không thể phân tích. Vui lòng kiểm tra lại thông tin.";
        };
    }

    private List<String> buildDietTips(String category) {
        return switch (category) {
            case "underweight" -> List.of(
                "Ăn 5-6 bữa nhỏ thay vì 3 bữa lớn",
                "Bổ sung protein từ thịt, trứng, sữa sau tập luyện",
                "Uống sinh tố trái cây + sữa để tăng calo",
                "Ăn thêm các loại hạt: óc chó, hạnh nhân, macca",
                "Tránh uống nước trước bữa ăn"
            );
            case "normal" -> List.of(
                "Duy trì chế độ ăn cân bằng 5 nhóm thực phẩm",
                "Uống đủ 2L nước mỗi ngày",
                "Ăn nhiều rau xanh và trái cây tươi",
                "Hạn chế đồ ăn chế biến sẵn",
                "Tập thể dục 30 phút mỗi ngày"
            );
            case "overweight" -> List.of(
                "Giảm khẩu phần tinh bột (cơm, bún, phở) 30%",
                "Thay thịt đỏ bằng cá và hải sản",
                "Ăn salad rau củ trước bữa chính",
                "Tránh ăn sau 20:00",
                "Đi bộ nhanh 45 phút mỗi ngày"
            );
            case "obese" -> List.of(
                "Loại bỏ hoàn toàn đồ chiên, nước ngọt, bánh kẹo",
                "Ăn nhiều rau xanh, xà lách, dưa chuột",
                "Chọn protein ít béo: ức gà, cá, đậu phụ",
                "Uống nước ấm chanh buổi sáng",
                "Tập thể dục ít nhất 60 phút mỗi ngày",
                "Tham khảo ý kiến bác sĩ dinh dưỡng"
            );
            default -> List.of();
        };
    }
}
