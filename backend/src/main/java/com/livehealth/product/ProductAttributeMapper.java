package com.livehealth.product;

import com.livehealth.product.dto.request.attribute.CreateAttributeRequestDto;
import com.livehealth.product.dto.request.attribute.UpdateAttributeRequestDto;
import com.livehealth.product.dto.response.attribute.AttributeResponseDto;
import com.livehealth.product.ProductAttribute;
import org.mapstruct.*;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProductAttributeMapper {

    @Mapping(target = "productId", source = "product.id")
    AttributeResponseDto toResponseDto(ProductAttribute attribute);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductAttribute toEntity(CreateAttributeRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateEntityFromDto(UpdateAttributeRequestDto request, @MappingTarget ProductAttribute attribute);
}
