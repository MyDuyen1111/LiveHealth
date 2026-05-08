package com.livehealth.product.dto.response.product;

import com.livehealth.product.dto.response.brand.BrandResponseDto;
import com.livehealth.product.dto.response.category.CategoryResponseDto;
import com.livehealth.product.dto.response.attribute.AttributeResponseDto;
import com.livehealth.product.dto.response.promotion.PromotionResponseDto;
import com.livehealth.product.dto.response.tag.TagResponseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponseDto {

    UUID id;

    String name;

    String description;

    double oldPrice;

    int stock;

    List<String> imageUrl;

    String sku;

    String createdAt;

    String updatedAt;

    BrandResponseDto brand;

    CategoryResponseDto category;

    List<TagResponseDto> tags;

    PromotionResponseDto promotion;

    List<AttributeResponseDto> additionalInfo;
}
