package com.livehealth.auth;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.livehealth.auth.dto.request.RegisterRequestDto;
import com.livehealth.user.dto.response.user.UserResponseDto;
import com.livehealth.user.User;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AuthMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "userHealth", ignore = true)
    @Mapping(target = "linkAvatar", ignore = true)
    @Mapping(target = "avatarPublicId", ignore = true)
    User registerRequestDtoToUser(RegisterRequestDto request);

    UserResponseDto userToUserResponseDto(User user);
}
