package com.livehealth.shared.web;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.shared.web.dto.request.testimonial.CreateTestimonialRequestDto;
import com.livehealth.shared.web.dto.request.testimonial.UpdateTestimonialRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.shared.web.dto.response.testimonial.TestimonialResponseDto;
import com.livehealth.shared.web.ClientTestimonial;
import com.livehealth.shared.web.ClientTestimonialMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.shared.web.ClientTestimonialRepository;
import com.livehealth.shared.web.ClientTestimonialService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.HttpStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import com.livehealth.shared.base.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClientTestimonialServiceImpl implements ClientTestimonialService {

    ClientTestimonialRepository testimonialRepository;
    ClientTestimonialMapper testimonialMapper;
    Cloudinary cloudinary;

    @Override
    public PaginationResponseDto<TestimonialResponseDto> getAllTestimonials(PaginationRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());

        Page<ClientTestimonial> testimonialPage = testimonialRepository.findAll(pageable);

        List<TestimonialResponseDto> dtos = testimonialPage.getContent()
                .stream()
                .map(testimonialMapper::toResponseDto)
                .collect(Collectors.toList());

        PagingMeta pagingMeta = new PagingMeta(
                testimonialPage.getTotalElements(),
                testimonialPage.getTotalPages(),
                request.getPageNum() + 1,
                request.getPageSize(),
                null,
                null);

        return new PaginationResponseDto<>(pagingMeta, dtos);
    }

    @Override
    public TestimonialResponseDto getTestimonialById(UUID id) {
        ClientTestimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ClientTestimonial.ERR_TESTIMONIAL_NOT_FOUND));

        return testimonialMapper.toResponseDto(testimonial);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TestimonialResponseDto createTestimonial(CreateTestimonialRequestDto request) {
        ClientTestimonial testimonial = testimonialMapper.toEntity(request);
        testimonial.setAvatarUrl("");
        testimonial.setCreatedAt(LocalDateTime.now());

        return testimonialMapper.toResponseDto(testimonialRepository.save(testimonial));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TestimonialResponseDto updateTestimonial(UUID id, UpdateTestimonialRequestDto request) {
        ClientTestimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ClientTestimonial.ERR_TESTIMONIAL_NOT_FOUND));

        testimonialMapper.updateEntityFromDto(request, testimonial);

        return testimonialMapper.toResponseDto(testimonialRepository.save(testimonial));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TestimonialResponseDto uploadAvatar(UUID id, MultipartFile file) {
        ClientTestimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ClientTestimonial.ERR_TESTIMONIAL_NOT_FOUND));

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) result.get("secure_url");
            testimonial.setAvatarUrl(imageUrl);
        } catch (Exception e) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.ERR_UPLOAD_IMAGE_FAIL);
        }

        return testimonialMapper.toResponseDto(testimonialRepository.save(testimonial));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResponseDto deleteTestimonial(UUID id) {
        ClientTestimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ClientTestimonial.ERR_TESTIMONIAL_NOT_FOUND));

        testimonialRepository.delete(testimonial);

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.ClientTestimonial.DELETE_TESTIMONIAL_SUCCESS);
    }
}
