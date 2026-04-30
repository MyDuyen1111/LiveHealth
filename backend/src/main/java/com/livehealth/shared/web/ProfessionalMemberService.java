package com.livehealth.shared.web;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.web.dto.request.member.CreateMemberRequestDto;
import com.livehealth.shared.web.dto.request.member.UpdateMemberRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.shared.web.dto.response.member.MemberResponseDto;
import com.livehealth.shared.base.MultipartFile;

import java.util.UUID;

public interface ProfessionalMemberService {

    PaginationResponseDto<MemberResponseDto> getAllMembers(PaginationRequestDto request);

    MemberResponseDto getMemberById(UUID id);

    MemberResponseDto createMember(CreateMemberRequestDto request);

    MemberResponseDto updateMember(UUID id, UpdateMemberRequestDto request);

    MemberResponseDto uploadAvatar(UUID id, MultipartFile file);

    CommonResponseDto deleteMember(UUID id);
}
