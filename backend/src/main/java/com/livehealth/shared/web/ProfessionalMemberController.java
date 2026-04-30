package com.livehealth.shared.web;

import com.livehealth.shared.base.RestApiV1;
import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.UrlConstant;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.web.dto.request.member.CreateMemberRequestDto;
import com.livehealth.shared.web.dto.request.member.UpdateMemberRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.shared.web.dto.response.member.MemberResponseDto;
import com.livehealth.shared.web.ProfessionalMemberService;
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
public class ProfessionalMemberController {

    ProfessionalMemberService memberService;

    // ==================== PUBLIC ====================

    @Operation(summary = "Lấy danh sách thành viên", description = "Lấy danh sách thành viên chuyên nghiệp (bác sĩ, cố vấn,...) có phân trang")
    @GET
    @Path(UrlConstant.ProfessionalMember.GET_ALL_MEMBERS)
    public Response getAllMembers(
            @BeanParam PaginationRequestDto request) {
        PaginationResponseDto<MemberResponseDto> response = memberService.getAllMembers(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Lấy chi tiết thành viên", description = "Lấy chi tiết thông tin của một đội ngũ")
    @GET
    @Path(UrlConstant.ProfessionalMember.GET_MEMBER_BY_ID)
    public Response getMemberById(@PathParam("id") UUID id) {
        MemberResponseDto response = memberService.getMemberById(id);
        return VsResponseUtil.success(response);
    }

    // ==================== ADMIN ====================

    @Operation(summary = "Tạo thành viên mới", description = "Thêm một thành viên chuyên gia mới", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.ProfessionalMember.CREATE_MEMBER)
    public Response createMember(
            @Valid CreateMemberRequestDto request) {
        MemberResponseDto response = memberService.createMember(request);
        return VsResponseUtil.success(HttpStatus.CREATED, response);
    }

    @Operation(summary = "Tải lên avatar của thành viên", description = "Cập nhật ảnh đại diện cho thành viên", security = @SecurityRequirement(name = "Bearer Token"))
    @POST
    @Path(UrlConstant.ProfessionalMember.UPDATE_MEMBER_AVATAR)
    @Consumes(jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA)
    public Response uploadAvatar(
            @PathParam("id") UUID id,
            @RestForm("file") FileUpload file) {
        MemberResponseDto response = memberService.uploadAvatar(id, new QuarkusMultipartFile(file));
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Cập nhật thông tin thành viên", description = "Cập nhật chức vụ hoặc tên của thành viên", security = @SecurityRequirement(name = "Bearer Token"))
    @PUT
    @Path(UrlConstant.ProfessionalMember.UPDATE_MEMBER)
    public Response updateMember(
            @PathParam("id") UUID id,
            @Valid UpdateMemberRequestDto request) {
        MemberResponseDto response = memberService.updateMember(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "Xóa diễn đàn", description = "Xóa thông tin của một thành viên trong đội ngũ", security = @SecurityRequirement(name = "Bearer Token"))
    @DELETE
    @Path(UrlConstant.ProfessionalMember.DELETE_MEMBER)
    public Response deleteMember(@PathParam("id") UUID id) {
        CommonResponseDto response = memberService.deleteMember(id);
        return VsResponseUtil.success(response);
    }
}
