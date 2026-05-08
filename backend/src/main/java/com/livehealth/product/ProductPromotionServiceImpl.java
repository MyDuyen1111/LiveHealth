package com.livehealth.product;

import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.product.dto.request.promotion.CreatePromotionRequestDto;
import com.livehealth.product.dto.request.promotion.UpdatePromotionRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.promotion.PromotionResponseDto;
import com.livehealth.product.Product;
import com.livehealth.product.ProductPromotion;
import com.livehealth.product.ProductPromotionMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.product.ProductPromotionRepository;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.ProductPromotionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductPromotionServiceImpl implements ProductPromotionService {

    ProductPromotionRepository promotionRepository;
    ProductRepository productRepository;
    ProductPromotionMapper promotionMapper;

    @Override
    public List<PromotionResponseDto> getPromotionsByProductId(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Product.ERR_PRODUCT_NOT_FOUND);
        }

        List<ProductPromotion> promotions = promotionRepository.findByProductId(productId);

        return promotions.stream()
                .map(promotionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponseDto getPromotionById(UUID id) {
        ProductPromotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ProductPromotion.ERR_PROMOTION_NOT_FOUND));

        return promotionMapper.toResponseDto(promotion);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PromotionResponseDto createPromotion(UUID productId, CreatePromotionRequestDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Product.ERR_PRODUCT_NOT_FOUND));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new VsException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu không thể sau ngày kết thúc");
        }

        Optional<ProductPromotion> existingActivePromotion = promotionRepository.findByProductIdAndIsActiveTrue(productId);
        if (existingActivePromotion.isPresent()) {
            // Can choose to deactivate the old one or throw an error. Let's throw an error to let Admin handle it manually.
            throw new VsException(HttpStatus.CONFLICT, ErrorMessage.ProductPromotion.ERR_PROMOTION_EXISTS_FOR_PRODUCT);
        }

        ProductPromotion promotion = promotionMapper.toEntity(request);
        promotion.setProduct(product);
        promotion.setActive(true);
        promotion.setCreatedAt(LocalDateTime.now());

        return promotionMapper.toResponseDto(promotionRepository.save(promotion));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PromotionResponseDto updatePromotion(UUID id, UpdatePromotionRequestDto request) {
        ProductPromotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ProductPromotion.ERR_PROMOTION_NOT_FOUND));

        promotionMapper.updateEntityFromDto(request, promotion);

        if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
            throw new VsException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu không thể sau ngày kết thúc");
        }

        return promotionMapper.toResponseDto(promotionRepository.save(promotion));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResponseDto deletePromotion(UUID id) {
        ProductPromotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ProductPromotion.ERR_PROMOTION_NOT_FOUND));

        promotionRepository.delete(promotion);

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.ProductPromotion.DELETE_PROMOTION_SUCCESS);
    }
}
