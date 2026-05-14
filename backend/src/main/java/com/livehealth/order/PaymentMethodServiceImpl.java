package com.livehealth.order;

import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.cart.dto.request.cart.PaymentMethodRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.cart.dto.response.cart.PaymentMethodResponseDto;
import com.livehealth.cart.PaymentMethod;
import com.livehealth.order.PaymentMethodMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.order.PaymentMethodRepository;
import com.livehealth.order.PaymentMethodService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentMethodServiceImpl implements PaymentMethodService {

    PaymentMethodRepository paymentMethodRepository;
    PaymentMethodMapper paymentMethodMapper;

    @Override
    public PaginationResponseDto<PaymentMethodResponseDto> getAllPaymentMethods(PaginationRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());
        Page<PaymentMethod> page = paymentMethodRepository.findAll(pageable);

        List<PaymentMethodResponseDto> dtos = page.getContent()
                .stream()
                .map(paymentMethodMapper::toResponseDto)
                .collect(Collectors.toList());

        PagingMeta pagingMeta = new PagingMeta(
                page.getTotalElements(),
                page.getTotalPages(),
                request.getPageNum() + 1,
                request.getPageSize(),
                null, null);

        return new PaginationResponseDto<>(pagingMeta, dtos);
    }

    @Override
    public PaymentMethodResponseDto getPaymentMethodById(UUID id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.PaymentMethod.ERR_PAYMENT_METHOD_NOT_FOUND));

        return paymentMethodMapper.toResponseDto(paymentMethod);
    }

    @Override
    public PaymentMethodResponseDto createPaymentMethod(PaymentMethodRequestDto request) {
        if (paymentMethodRepository.existsByName(request.getName())) {
            throw new VsException(HttpStatus.CONFLICT, ErrorMessage.PaymentMethod.ERR_NAME_EXISTED);
        }
        PaymentMethod paymentMethod = paymentMethodMapper.toEntity(request);
        return paymentMethodMapper.toResponseDto(paymentMethodRepository.save(paymentMethod));
    }

    @Override
    public PaymentMethodResponseDto updatePaymentMethod(UUID id, PaymentMethodRequestDto request) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.PaymentMethod.ERR_PAYMENT_METHOD_NOT_FOUND));

        if (!paymentMethod.getName().equals(request.getName()) && paymentMethodRepository.existsByName(request.getName())) {
            throw new VsException(HttpStatus.CONFLICT, ErrorMessage.PaymentMethod.ERR_NAME_EXISTED);
        }
        paymentMethodMapper.updateEntityFromDto(request, paymentMethod);
        return paymentMethodMapper.toResponseDto(paymentMethodRepository.save(paymentMethod));
    }

    @Override
    public CommonResponseDto deletePaymentMethod(UUID id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.PaymentMethod.ERR_PAYMENT_METHOD_NOT_FOUND));
        paymentMethodRepository.delete(paymentMethod);
        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.PaymentMethod.DELETE_PAYMENT_METHOD_SUCCESS);
    }
}
