package com.livehealth.order;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.cart.dto.request.cart.PaymentMethodRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.cart.dto.response.cart.PaymentMethodResponseDto;

import java.util.UUID;

public interface PaymentMethodService {

    PaginationResponseDto<PaymentMethodResponseDto> getAllPaymentMethods(PaginationRequestDto request);

    PaymentMethodResponseDto getPaymentMethodById(UUID id);

    PaymentMethodResponseDto createPaymentMethod(PaymentMethodRequestDto request);

    PaymentMethodResponseDto updatePaymentMethod(UUID id, PaymentMethodRequestDto request);

    CommonResponseDto deletePaymentMethod(UUID id);
}
