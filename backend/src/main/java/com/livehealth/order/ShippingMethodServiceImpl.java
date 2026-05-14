package com.livehealth.order;

import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.cart.dto.request.cart.ShippingMethodRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.cart.dto.response.cart.ShippingMethodResponseDto;
import com.livehealth.cart.ShippingMethod;
import com.livehealth.order.ShippingMethodMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.order.ShippingMethodRepository;
import com.livehealth.order.ShippingMethodService;
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
public class ShippingMethodServiceImpl implements ShippingMethodService {

    ShippingMethodRepository shippingMethodRepository;
    ShippingMethodMapper shippingMethodMapper;

    @Override
    public PaginationResponseDto<ShippingMethodResponseDto> getAllShippingMethods(PaginationRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());
        Page<ShippingMethod> page = shippingMethodRepository.findAll(pageable);

        List<ShippingMethodResponseDto> dtos = page.getContent()
                .stream()
                .map(shippingMethodMapper::toResponseDto)
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
    public ShippingMethodResponseDto getShippingMethodById(UUID id) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ShippingMethod.ERR_SHIPPING_METHOD_NOT_FOUND));

        return shippingMethodMapper.toResponseDto(shippingMethod);
    }

    @Override
    public ShippingMethodResponseDto createShippingMethod(ShippingMethodRequestDto request) {
        if (shippingMethodRepository.existsByName(request.getName())) {
            throw new VsException(HttpStatus.CONFLICT, "Shipping method name already existed");
        }
        ShippingMethod shippingMethod = shippingMethodMapper.toEntity(request);
        return shippingMethodMapper.toResponseDto(shippingMethodRepository.save(shippingMethod));
    }

    @Override
    public ShippingMethodResponseDto updateShippingMethod(UUID id, ShippingMethodRequestDto request) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ShippingMethod.ERR_SHIPPING_METHOD_NOT_FOUND));

        if (!shippingMethod.getName().equals(request.getName()) && shippingMethodRepository.existsByName(request.getName())) {
            throw new VsException(HttpStatus.CONFLICT, "Shipping method name already existed");
        }

        shippingMethodMapper.updateEntityFromDto(request, shippingMethod);
        return shippingMethodMapper.toResponseDto(shippingMethodRepository.save(shippingMethod));
    }

    @Override
    public CommonResponseDto deleteShippingMethod(UUID id) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ShippingMethod.ERR_SHIPPING_METHOD_NOT_FOUND));

        shippingMethodRepository.delete(shippingMethod);
        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.ShippingMethod.DELETE_SHIPPING_METHOD_SUCCESS);
    }
}
