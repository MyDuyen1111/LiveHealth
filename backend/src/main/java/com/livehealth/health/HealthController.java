package com.livehealth.health;

import com.livehealth.health.dto.HealthAnalysisRequestDto;
import com.livehealth.health.dto.HealthAnalysisResponseDto;
import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HealthController {

    HealthService healthService;

    @Operation(summary = "Phân tích sức khỏe AI",
               description = "Tính BMI, BMR, TDEE và gợi ý sản phẩm phù hợp dựa trên thể trạng")
    @POST
    @Path("/health/analyze")
    public Response analyze(
            @Valid HealthAnalysisRequestDto request) {
        HealthAnalysisResponseDto response = healthService.analyze(request);
        return VsResponseUtil.success(response);
    }
}
