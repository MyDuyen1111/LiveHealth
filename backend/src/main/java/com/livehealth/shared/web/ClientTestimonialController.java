package com.livehealth.shared.web;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.web.dto.request.testimonial.CreateTestimonialRequestDto;
import com.livehealth.shared.web.dto.request.testimonial.UpdateTestimonialRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.shared.web.dto.response.testimonial.TestimonialResponseDto;
import com.livehealth.shared.web.ClientTestimonialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.HttpStatus;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import com.livehealth.shared.base.QuarkusMultipartFile;
import com.livehealth.shared.base.MultipartFile;

import java.util.UUID;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClientTestimonialController {

    ClientTestimonialService testimonialService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách đánh giá từ khách hàng", description = "Lấy danh sách đánh giá từ khách hàng có phân trang")
    @GET
    @Path(UrlConstant.ClientTestimonial.GET_ALL_TESTIMONIALS)
    public Response getAllTestimonials(
            @BeanParam PaginationRequestDto request) {
        PaginationResponseDto<TestimonialResponseDto> response = testimonialService.getAllTestimonials(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết đánh giá", description = "Lấy thông tin chi tiết của một đánh giá bằng ID")
    @GET
    @Path(UrlConstant.ClientTestimonial.GET_TESTIMONIAL_BY_ID)
    public Response getTestimonialById(@PathParam("id") UUID id) {
        TestimonialResponseDto response = testimonialService.getTestimonialById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Tạo đánh giá mới", description = "Dùng để tạo một đánh giá mới từ khách hàng", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.ClientTestimonial.CREATE_TESTIMONIAL)
    public Response createTestimonial(
            @Valid CreateTestimonialRequestDto request) {
        TestimonialResponseDto response = testimonialService.createTestimonial(request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Cập nhật avatar đánh giá", description = "Dùng để tải lên hoặc cập nhật hình ảnh avatar của khách hàng", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.ClientTestimonial.UPDATE_TESTIMONIAL_AVATAR)
    @Consumes(jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA)
    public Response uploadAvatar(
            @PathParam("id") UUID id,
            @RestForm("file") FileUpload file) {
        TestimonialResponseDto response = testimonialService.uploadAvatar(id, new QuarkusMultipartFile(file));
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Cập nhật đánh giá", description = "Dùng để cập nhật nội dung đánh giá", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.ClientTestimonial.UPDATE_TESTIMONIAL)
    public Response updateTestimonial(
            @PathParam("id") UUID id,
            @Valid UpdateTestimonialRequestDto request) {
        TestimonialResponseDto response = testimonialService.updateTestimonial(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa đánh giá", description = "Dùng để xóa một đánh giá của khách hàng", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path(UrlConstant.ClientTestimonial.DELETE_TESTIMONIAL)
    public Response deleteTestimonial(@PathParam("id") UUID id) {
        CommonResponseDto response = testimonialService.deleteTestimonial(id);
        return VsResponseUtil.success(response);
    }
}
