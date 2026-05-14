package com.livehealth.order;

import com.livehealth.cart.dto.request.cart.PaymentMethodRequestDto;
import com.livehealth.cart.dto.response.cart.PaymentMethodResponseDto;
import com.livehealth.cart.PaymentMethod;
import org.mapstruct.*;

@Mapper(componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PaymentMethodMapper {

    PaymentMethodResponseDto toResponseDto(PaymentMethod paymentMethod);

    @Mapping(target = "id", ignore = true)
    PaymentMethod toEntity(PaymentMethodRequestDto request);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(PaymentMethodRequestDto request, @MappingTarget PaymentMethod paymentMethod);
}
