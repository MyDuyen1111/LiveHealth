package com.livehealth.product;

import com.livehealth.product.dto.request.brand.CreateBrandRequestDto;
import com.livehealth.product.dto.request.brand.UpdateBrandRequestDto;
import com.livehealth.product.dto.response.brand.BrandResponseDto;
import com.livehealth.product.Brand;
import org.mapstruct.*;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BrandMapper {

    @Mapping(target = "totalProducts", expression = "java(brand.getProducts() != null ? brand.getProducts().size() : 0)")
    BrandResponseDto brandToBrandResponseDto(Brand brand);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    Brand createBrandRequestDtoToBrand(CreateBrandRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    void updateBrandFromDto(UpdateBrandRequestDto request, @MappingTarget Brand brand);
}
