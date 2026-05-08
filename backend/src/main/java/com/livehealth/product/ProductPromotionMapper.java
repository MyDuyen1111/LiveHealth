package com.livehealth.product;

import com.livehealth.product.dto.request.promotion.CreatePromotionRequestDto;
import com.livehealth.product.dto.request.promotion.UpdatePromotionRequestDto;
import com.livehealth.product.dto.response.promotion.PromotionResponseDto;
import com.livehealth.product.ProductPromotion;
import org.mapstruct.*;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProductPromotionMapper {

    @Mapping(target = "productId", source = "product.id")
    PromotionResponseDto toResponseDto(ProductPromotion promotion);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ProductPromotion toEntity(CreatePromotionRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(UpdatePromotionRequestDto request, @MappingTarget ProductPromotion promotion);
}
