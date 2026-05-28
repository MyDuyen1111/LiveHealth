package com.livehealth.health;

import com.livehealth.health.dto.HealthAnalysisRequestDto;
import com.livehealth.health.dto.HealthAnalysisResponseDto;

public interface HealthService {
    HealthAnalysisResponseDto analyze(HealthAnalysisRequestDto request);
}
