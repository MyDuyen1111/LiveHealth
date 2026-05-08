package com.livehealth.product;

import com.livehealth.product.dto.request.review.CreateReviewRequestDto;
import com.livehealth.product.dto.request.review.UpdateReviewRequestDto;
import com.livehealth.product.dto.response.review.ReviewResponseDto;
import com.livehealth.product.Review;
import org.mapstruct.*;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ReviewMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", source = "user.firstName")
    ReviewResponseDto toResponseDto(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(CreateReviewRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(UpdateReviewRequestDto request, @MappingTarget Review review);
}
