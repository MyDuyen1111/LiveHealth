package com.livehealth.product;

import com.livehealth.product.dto.request.tag.CreateTagRequestDto;
import com.livehealth.product.dto.request.tag.UpdateTagRequestDto;
import com.livehealth.product.dto.response.tag.TagResponseDto;
import com.livehealth.product.Tag;
import org.mapstruct.*;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TagMapper {

    @Mapping(target = "totalProducts", expression = "java(tag.getProducts() != null ? tag.getProducts().size() : 0)")
    TagResponseDto tagToTagResponseDto(Tag tag);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Tag createTagRequestDtoToTag(CreateTagRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateTagFromDto(UpdateTagRequestDto request, @MappingTarget Tag tag);
}
