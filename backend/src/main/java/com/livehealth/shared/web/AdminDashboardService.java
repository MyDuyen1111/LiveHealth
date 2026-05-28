package com.livehealth.shared.web;

import com.livehealth.shared.web.dto.response.dashboard.DashboardStatsResponseDto;

public interface AdminDashboardService {
    DashboardStatsResponseDto getDashboardStats();
}
