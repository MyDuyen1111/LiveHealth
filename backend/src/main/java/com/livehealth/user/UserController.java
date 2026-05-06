package com.livehealth.user;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.user.dto.request.user.personalInformation.PersonalInformationRequestDto;
import com.livehealth.user.dto.request.user.profile.UpdateProfileRequestDto;
import com.livehealth.user.dto.request.user.profile.ChangePasswordRequestDto;
import com.livehealth.user.dto.response.user.UserResponseDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.shared.security.SecurityUtils;
import com.livehealth.user.UserService;
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

import java.util.UUID;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

        UserService userService;

        @Operation(summary = "Điền thông tin cá nhân", description = "Dùng để người dùng điền thông tin cá nhân", security = @SecurityRequirement(name = "Bearer Token"))
        @POST
        @Path(UrlConstant.User.FILL_PERSONAL_INFORMATION)
        public Response fillPersonalInformation(
                        @Valid PersonalInformationRequestDto request) {
                UUID userId = SecurityUtils.getCurrentUserId();
                UserResponseDto response = userService.personalInformation(userId, request);
                return VsResponseUtil.success(response);
        }

        @Operation(summary = "Tải lên ảnh đại diện", description = "Dùng để người dùng tải lên ảnh đại diện", security = @SecurityRequirement(name = "Bearer Token"))
        @POST
        @Path(UrlConstant.User.UPLOAD_AVATAR)
        @Consumes(jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA)
        public Response uploadAvatar(
                        @RestForm("file") FileUpload file) {
                UUID userId = SecurityUtils.getCurrentUserId();
                UserResponseDto response = userService.uploadAvatar(userId, new QuarkusMultipartFile(file));
                return VsResponseUtil.success(response);
        }

        @Operation(summary = "Lấy thông tin profile (thông tin user và user health)", description = "Dùng để người dùng lấy thông tin profile đầy đủ (thông tin cá nhân + sức khỏe)", security = @SecurityRequirement(name = "Bearer Token"))
        @GET
        @Path(UrlConstant.User.GET_PROFILE)
        public Response getMyProfile() {
                UUID userId = SecurityUtils.getCurrentUserId();
                UserResponseDto response = userService.getMyProfile(userId);
                return VsResponseUtil.success(response);
        }

        @Operation(summary = "Cập nhật thông tin profile", description = "Dùng để người dùng cập nhật thông tin cá nhân và sức khỏe với xác nhận mật khẩu", security = @SecurityRequirement(name = "Bearer Token"))
        @PUT
        @Path(UrlConstant.User.UPDATE_PROFILE)
        public Response updateProfile(
                        @Valid UpdateProfileRequestDto request) {
                UUID userId = SecurityUtils.getCurrentUserId();
                UserResponseDto response = userService.updateProfile(request, userId);
                return VsResponseUtil.success(response);
        }

        @Operation(summary = "Cập nhật địa chỉ thanh toán", description = "Dùng để người dùng cập nhật địa chỉ thanh toán mặc định", security = @SecurityRequirement(name = "Bearer Token"))
        @PUT
        @Path(UrlConstant.User.UPDATE_BILLING_ADDRESS)
        public Response updateBillingAddress(
                        @Valid com.livehealth.order.dto.request.order.AddressDto request) {
                UUID userId = SecurityUtils.getCurrentUserId();
                UserResponseDto response = userService.updateBillingAddress(userId, request);
                return VsResponseUtil.success(response);
        }

        @Operation(summary = "Đổi mật khẩu", description = "Dùng để người dùng thay đổi mật khẩu", security = @SecurityRequirement(name = "Bearer Token"))
        @PUT
        @Path(UrlConstant.User.CHANGE_PASSWORD)
        public Response changePassword(
                        @Valid ChangePasswordRequestDto request) {
                UUID userId = SecurityUtils.getCurrentUserId();
                CommonResponseDto response = userService.changePassword(userId, request);
                return VsResponseUtil.success(response);
        }
}
