package com.livehealth.order;

import com.livehealth.cart.dto.request.cart.ShippingMethodRequestDto;
import com.livehealth.cart.dto.response.cart.ShippingMethodResponseDto;
import com.livehealth.cart.ShippingMethod;
import org.mapstruct.*;

@Mapper(componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ShippingMethodMapper {

    ShippingMethodResponseDto toResponseDto(ShippingMethod shippingMethod);

    @Mapping(target = "id", ignore = true)
    ShippingMethod toEntity(ShippingMethodRequestDto request);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ShippingMethodRequestDto request, @MappingTarget ShippingMethod shippingMethod);
}
