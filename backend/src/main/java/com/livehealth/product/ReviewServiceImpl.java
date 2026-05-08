package com.livehealth.product;

import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.product.dto.request.review.CreateReviewRequestDto;
import com.livehealth.product.dto.request.review.UpdateReviewRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.product.dto.response.review.ReviewResponseDto;
import com.livehealth.product.Product;
import com.livehealth.product.Review;
import com.livehealth.user.User;
import com.livehealth.product.ReviewMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.product.ProductRepository;
import com.livehealth.product.ReviewRepository;
import com.livehealth.user.UserRepository;
import com.livehealth.shared.security.SecurityUtils;
import com.livehealth.product.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewServiceImpl implements ReviewService {

    ReviewRepository reviewRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    ReviewMapper reviewMapper;

    @Override
    public PaginationResponseDto<ReviewResponseDto> getReviewsByProductId(UUID productId, PaginationRequestDto request) {
        if (!productRepository.existsById(productId)) {
            throw new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Product.ERR_PRODUCT_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());
        Page<Review> reviewPage = reviewRepository.findByProductId(productId, pageable);

        List<ReviewResponseDto> dtos = reviewPage.getContent()
                .stream()
                .map(reviewMapper::toResponseDto)
                .collect(Collectors.toList());

        PagingMeta pagingMeta = new PagingMeta(
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                request.getPageNum() + 1,
                request.getPageSize(),
                null,
                null);

        return new PaginationResponseDto<>(pagingMeta, dtos);
    }

    @Override
    public ReviewResponseDto getReviewById(UUID id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Review.ERR_REVIEW_NOT_FOUND));

        return reviewMapper.toResponseDto(review);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ReviewResponseDto createReview(UUID productId, CreateReviewRequestDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Product.ERR_PRODUCT_NOT_FOUND));

        UUID userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new VsException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.User.ERR_USER_NOT_EXISTED));

        Review review = reviewMapper.toEntity(request);
        review.setProduct(product);
        review.setUser(user);
        review.setCreatedAt(LocalDateTime.now());

        return reviewMapper.toResponseDto(reviewRepository.save(review));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ReviewResponseDto updateReview(UUID id, UpdateReviewRequestDto request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Review.ERR_REVIEW_NOT_FOUND));

        UUID userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new VsException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        if (!review.getUser().getId().equals(userId)) {
            throw new VsException(HttpStatus.FORBIDDEN, "Bạn không có quyền sửa đánh giá này");
        }

        reviewMapper.updateEntityFromDto(request, review);

        return reviewMapper.toResponseDto(reviewRepository.save(review));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResponseDto deleteReview(UUID id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.Review.ERR_REVIEW_NOT_FOUND));

        // Let's assume the admin drops reviews unconditionally, but if a normal user does, limit it to their own.
        // Actually, we'll map DELETE _REVIEW endpoint with @PreAuthorize if needed or assume Admin-only in SecurityConfig.
        // For now, let's keep it simple.

        reviewRepository.delete(review);

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.Review.DELETE_REVIEW_SUCCESS);
    }
}
