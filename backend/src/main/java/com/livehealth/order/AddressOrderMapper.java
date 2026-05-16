package com.livehealth.order;

import com.livehealth.order.dto.request.order.AddressDto;
import com.livehealth.order.dto.response.order.AddressResponseDto;
import com.livehealth.user.Address;
import org.mapstruct.*;

@Mapper(componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AddressOrderMapper {

    AddressResponseDto toResponseDto(Address address);

    @Mapping(target = "id", ignore = true)
    Address toEntity(AddressDto request);

}
