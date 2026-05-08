package com.livehealth.product;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.product.dto.request.review.CreateReviewRequestDto;
import com.livehealth.product.dto.request.review.UpdateReviewRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.review.ReviewResponseDto;

import java.util.UUID;

public interface ReviewService {

    PaginationResponseDto<ReviewResponseDto> getReviewsByProductId(UUID productId, PaginationRequestDto request);

    ReviewResponseDto getReviewById(UUID id);

    ReviewResponseDto createReview(UUID productId, CreateReviewRequestDto request);

    ReviewResponseDto updateReview(UUID id, UpdateReviewRequestDto request);

    CommonResponseDto deleteReview(UUID id);
}
