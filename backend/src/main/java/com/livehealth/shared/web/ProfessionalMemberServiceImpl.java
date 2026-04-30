package com.livehealth.shared.web;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.constant.SuccessMessage;
import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.shared.dto.pagination.PagingMeta;
import com.livehealth.shared.web.dto.request.member.CreateMemberRequestDto;
import com.livehealth.shared.web.dto.request.member.UpdateMemberRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.shared.web.dto.response.member.MemberResponseDto;
import com.livehealth.shared.web.ProfessionalMember;
import com.livehealth.shared.web.ProfessionalMemberMapper;
import com.livehealth.shared.exception.VsException;
import com.livehealth.shared.web.ProfessionalMemberRepository;
import com.livehealth.shared.web.ProfessionalMemberService;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfessionalMemberServiceImpl implements ProfessionalMemberService {

    ProfessionalMemberRepository memberRepository;
    ProfessionalMemberMapper memberMapper;
    Cloudinary cloudinary;

    @Override
    public PaginationResponseDto<MemberResponseDto> getAllMembers(PaginationRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());

        Page<ProfessionalMember> memberPage = memberRepository.findAll(pageable);

        List<MemberResponseDto> dtos = memberPage.getContent()
                .stream()
                .map(memberMapper::toResponseDto)
                .collect(Collectors.toList());

        PagingMeta pagingMeta = new PagingMeta(
                memberPage.getTotalElements(),
                memberPage.getTotalPages(),
                request.getPageNum() + 1,
                request.getPageSize(),
                null,
                null);

        return new PaginationResponseDto<>(pagingMeta, dtos);
    }

    @Override
    public MemberResponseDto getMemberById(UUID id) {
        ProfessionalMember member = memberRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ProfessionalMember.ERR_MEMBER_NOT_FOUND));

        return memberMapper.toResponseDto(member);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MemberResponseDto createMember(CreateMemberRequestDto request) {
        ProfessionalMember member = memberMapper.toEntity(request);
        member.setAvatarUrl("");

        return memberMapper.toResponseDto(memberRepository.save(member));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MemberResponseDto updateMember(UUID id, UpdateMemberRequestDto request) {
        ProfessionalMember member = memberRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ProfessionalMember.ERR_MEMBER_NOT_FOUND));

        memberMapper.updateEntityFromDto(request, member);

        return memberMapper.toResponseDto(memberRepository.save(member));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MemberResponseDto uploadAvatar(UUID id, MultipartFile file) {
        ProfessionalMember member = memberRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ProfessionalMember.ERR_MEMBER_NOT_FOUND));

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) result.get("secure_url");
            member.setAvatarUrl(imageUrl);
        } catch (Exception e) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.ERR_UPLOAD_IMAGE_FAIL);
        }

        return memberMapper.toResponseDto(memberRepository.save(member));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResponseDto deleteMember(UUID id) {
        ProfessionalMember member = memberRepository.findById(id)
                .orElseThrow(() -> new VsException(HttpStatus.NOT_FOUND, ErrorMessage.ProfessionalMember.ERR_MEMBER_NOT_FOUND));

        memberRepository.delete(member);

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.ProfessionalMember.DELETE_MEMBER_SUCCESS);
    }
}
