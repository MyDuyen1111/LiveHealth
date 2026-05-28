package com.livehealth.shared.web;

import com.livehealth.order.Order;
import com.livehealth.order.OrderMapper;
import com.livehealth.order.OrderRepository;
import com.livehealth.product.ProductRepository;
import com.livehealth.user.UserRepository;
import com.livehealth.shared.web.dto.response.dashboard.DashboardStatsResponseDto;
import com.livehealth.order.dto.response.order.OrderResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    OrderRepository orderRepository;
    UserRepository userRepository;
    ProductRepository productRepository;
    OrderMapper orderMapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public DashboardStatsResponseDto getDashboardStats() {
        double totalRevenue = orderRepository.calculateTotalRevenue();
        long totalOrders = orderRepository.count();
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();

        List<Order> recentOrders = orderRepository.findRecentOrders(5);
        List<OrderResponseDto> recentOrderDtos = recentOrders.stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());

        return DashboardStatsResponseDto.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .recentOrders(recentOrderDtos)
                .build();
    }
}
