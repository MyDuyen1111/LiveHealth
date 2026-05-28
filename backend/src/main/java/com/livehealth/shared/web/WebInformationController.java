package com.livehealth.shared.web;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.shared.web.dto.request.webinfo.WebInformationRequestDto;
import com.livehealth.shared.web.dto.response.webinfo.WebInformationResponseDto;
import com.livehealth.shared.web.WebInformationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import com.livehealth.shared.base.QuarkusMultipartFile;
import com.livehealth.shared.base.MultipartFile;

@RestApiV1
@Path("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebInformationController {

    WebInformationService webInformationService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy thông tin website", description = "API cho phép lấy các thông tin chung của website (Logo, địa chỉ, lượt follow...)")
    @GET
    @Path(UrlConstant.WebInformation.GET_WEB_INFORMATION)
    public Response getWebInformation() {
        WebInformationResponseDto response = webInformationService.getWebInformation();
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Cập nhật logo website", description = "Cập nhật hình ảnh logo cho website", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.WebInformation.UPDATE_WEB_LOGO)
    @Consumes(jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA)
    public Response uploadLogo(
            @RestForm("file") FileUpload file) {
        WebInformationResponseDto response = webInformationService.uploadLogo(new QuarkusMultipartFile(file));
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Cập nhật thông tin website", description = "Cập nhật chi tiết các trường như địa chỉ, lượt khách hàng, thành viên,...", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.WebInformation.UPDATE_WEB_INFORMATION)
    public Response updateWebInformation(
            @Valid WebInformationRequestDto request) {
        WebInformationResponseDto response = webInformationService.updateWebInformation(request);
        return VsResponseUtil.success(response);
    }
}
