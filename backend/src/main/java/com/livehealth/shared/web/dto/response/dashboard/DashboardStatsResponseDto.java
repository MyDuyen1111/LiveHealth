package com.livehealth.shared.web.dto.response.dashboard;

import com.livehealth.order.dto.response.order.OrderResponseDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardStatsResponseDto {
    double totalRevenue;
    long totalOrders;
    long totalUsers;
    long totalProducts;
    List<OrderResponseDto> recentOrders;
}
