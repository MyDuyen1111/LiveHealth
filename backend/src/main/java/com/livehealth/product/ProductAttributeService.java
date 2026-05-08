package com.livehealth.product;

import com.livehealth.product.dto.request.attribute.CreateAttributeRequestDto;
import com.livehealth.product.dto.request.attribute.UpdateAttributeRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.attribute.AttributeResponseDto;

import java.util.List;
import java.util.UUID;

public interface ProductAttributeService {

    List<AttributeResponseDto> getAttributesByProductId(UUID productId);

    AttributeResponseDto getAttributeById(UUID id);

    AttributeResponseDto createAttribute(UUID productId, CreateAttributeRequestDto request);

    AttributeResponseDto updateAttribute(UUID id, UpdateAttributeRequestDto request);

    CommonResponseDto deleteAttribute(UUID id);
}
