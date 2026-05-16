package com.livehealth.order;

import com.livehealth.order.dto.response.order.OrderItemResponseDto;
import com.livehealth.order.dto.response.order.OrderResponseDto;
import com.livehealth.order.Order;
import com.livehealth.order.OrderItem;
import org.mapstruct.*;

@Mapper(componentModel = "cdi",
    uses = {AddressOrderMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(order.getUser() != null ? order.getUser().getFirstName() + \" \" + order.getUser().getLastName() : null)")
    @Mapping(target = "paymentMethodId", source = "paymentMethod.id")
    @Mapping(target = "paymentMethodName", source = "paymentMethod.name")
    @Mapping(target = "contactEmail", expression = "java(order.getContactEmail() != null && !order.getContactEmail().isBlank() ? order.getContactEmail() : (order.getUser() != null ? order.getUser().getEmail() : null))")
    @Mapping(target = "contactPhone", expression = "java(order.getContactPhone() != null && !order.getContactPhone().isBlank() ? order.getContactPhone() : (order.getUser() != null ? order.getUser().getPhone() : null))")
    OrderResponseDto toResponseDto(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", expression = "java(orderItem.getProduct().getImageUrl() != null && !orderItem.getProduct().getImageUrl().isEmpty() ? orderItem.getProduct().getImageUrl().get(0) : null)")
    OrderItemResponseDto orderItemToResponseDto(OrderItem orderItem);
}
