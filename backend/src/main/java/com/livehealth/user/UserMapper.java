package com.livehealth.user;

import com.livehealth.user.dto.request.admin.UpdateUserRequestDto;
import com.livehealth.user.dto.request.user.personalInformation.UpdatePersonalInformationRequestDto;
import org.mapstruct.*;

import com.livehealth.user.dto.request.admin.CreateUserRequestDto;
import com.livehealth.user.dto.response.user.UserResponseDto;
import com.livehealth.user.User;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    UserResponseDto userToUserResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "linkAvatar", ignore = true)
    @Mapping(target = "avatarPublicId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "userHealth", ignore = true)
    User createUserRequestDtoToUser(CreateUserRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "linkAvatar", ignore = true)
    @Mapping(target = "avatarPublicId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "userHealth", ignore = true)
    void updateUserFromDto(UpdateUserRequestDto request, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "linkAvatar", ignore = true)
    @Mapping(target = "avatarPublicId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "userHealth", ignore = true)
    void updateUserFromPersonalInformationDto(UpdatePersonalInformationRequestDto request, @MappingTarget User user);
}
