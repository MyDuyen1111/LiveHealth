package com.livehealth.product;

import com.livehealth.product.dto.request.promotion.CreatePromotionRequestDto;
import com.livehealth.product.dto.request.promotion.UpdatePromotionRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.promotion.PromotionResponseDto;

import java.util.List;
import java.util.UUID;

public interface ProductPromotionService {

    List<PromotionResponseDto> getPromotionsByProductId(UUID productId);

    PromotionResponseDto getPromotionById(UUID id);

    PromotionResponseDto createPromotion(UUID productId, CreatePromotionRequestDto request);

    PromotionResponseDto updatePromotion(UUID id, UpdatePromotionRequestDto request);

    CommonResponseDto deletePromotion(UUID id);
}
