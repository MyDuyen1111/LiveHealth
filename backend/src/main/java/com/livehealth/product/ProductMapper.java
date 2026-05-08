package com.livehealth.product;

import com.livehealth.product.dto.request.product.CreateProductRequestDto;
import com.livehealth.product.dto.request.product.UpdateProductRequestDto;
import com.livehealth.product.dto.response.product.ProductResponseDto;
import com.livehealth.product.Product;
import org.mapstruct.*;

@Mapper(componentModel = "cdi",
    uses = {BrandMapper.class, CategoryMapper.class, TagMapper.class, ProductPromotionMapper.class, ProductAttributeMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProductMapper {

    ProductResponseDto toResponseDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "promotion", ignore = true)
    @Mapping(target = "additionalInfo", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(CreateProductRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "promotion", ignore = true)
    @Mapping(target = "additionalInfo", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "sku", ignore = true) // Cập nhật SKU nếu cần thì cho phép, tạm bỏ qua
    void updateEntityFromDto(UpdateProductRequestDto request, @MappingTarget Product product);
}
