package com.livehealth.shared.web;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.web.dto.request.testimonial.CreateTestimonialRequestDto;
import com.livehealth.shared.web.dto.request.testimonial.UpdateTestimonialRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.shared.web.dto.response.testimonial.TestimonialResponseDto;
import com.livehealth.shared.base.MultipartFile;

import java.util.UUID;

public interface ClientTestimonialService {

    PaginationResponseDto<TestimonialResponseDto> getAllTestimonials(PaginationRequestDto request);

    TestimonialResponseDto getTestimonialById(UUID id);

    TestimonialResponseDto createTestimonial(CreateTestimonialRequestDto request);

    TestimonialResponseDto updateTestimonial(UUID id, UpdateTestimonialRequestDto request);

    TestimonialResponseDto uploadAvatar(UUID id, MultipartFile file);

    CommonResponseDto deleteTestimonial(UUID id);
}
