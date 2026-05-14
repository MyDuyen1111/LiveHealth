package com.livehealth.order;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.cart.dto.request.cart.ShippingMethodRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.cart.dto.response.cart.ShippingMethodResponseDto;

import java.util.UUID;

public interface ShippingMethodService {

    PaginationResponseDto<ShippingMethodResponseDto> getAllShippingMethods(PaginationRequestDto request);

    ShippingMethodResponseDto getShippingMethodById(UUID id);

    ShippingMethodResponseDto createShippingMethod(ShippingMethodRequestDto request);

    ShippingMethodResponseDto updateShippingMethod(UUID id, ShippingMethodRequestDto request);

    CommonResponseDto deleteShippingMethod(UUID id);
}
