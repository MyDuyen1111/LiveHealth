package com.livehealth.shared.web;

import com.livehealth.shared.web.dto.request.member.CreateMemberRequestDto;
import com.livehealth.shared.web.dto.request.member.UpdateMemberRequestDto;
import com.livehealth.shared.web.dto.response.member.MemberResponseDto;
import com.livehealth.shared.web.ProfessionalMember;
import org.mapstruct.*;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProfessionalMemberMapper {

    MemberResponseDto toResponseDto(ProfessionalMember member);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    ProfessionalMember toEntity(CreateMemberRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    void updateEntityFromDto(UpdateMemberRequestDto request, @MappingTarget ProfessionalMember member);
}
