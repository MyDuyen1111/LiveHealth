package com.livehealth.shared.web;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.web.dto.response.dashboard.DashboardStatsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@RestApiV1
@Path("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminDashboardController {

    AdminDashboardService dashboardService;

    @Operation(summary = "Lấy thống kê dashboard admin", description = "Lấy tổng quan về doanh thu, số đơn hàng, khách hàng và danh sách đơn hàng gần đây", security = @SecurityRequirement(name = "Bearer Token"))
    @GET
    @Path("/stats")
    public Response getDashboardStats() {
        DashboardStatsResponseDto response = dashboardService.getDashboardStats();
        return VsResponseUtil.success(response);
    }
}
