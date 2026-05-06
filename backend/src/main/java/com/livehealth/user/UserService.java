package com.livehealth.user;

import com.livehealth.shared.dto.pagination.PaginationRequestDto;
import com.livehealth.shared.dto.pagination.PaginationResponseDto;
import com.livehealth.user.dto.request.admin.CreateUserRequestDto;
import com.livehealth.user.dto.request.admin.UpdateUserRequestDto;
import com.livehealth.user.dto.request.user.personalInformation.PersonalInformationRequestDto;
import com.livehealth.user.dto.request.user.profile.UpdateProfileRequestDto;
import com.livehealth.user.dto.request.user.profile.ChangePasswordRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.user.dto.response.user.UserResponseDto;
import com.livehealth.shared.base.MultipartFile;

import java.util.UUID;

public interface UserService {

        UserResponseDto personalInformation(UUID userId, PersonalInformationRequestDto request);

        UserResponseDto uploadAvatar(UUID userId, MultipartFile file);

        UserResponseDto getMyProfile(UUID userId);

        UserResponseDto updateProfile(UpdateProfileRequestDto request, UUID userId);

        UserResponseDto updateBillingAddress(UUID userId, com.livehealth.order.dto.request.order.AddressDto request);

        CommonResponseDto changePassword(UUID userId, ChangePasswordRequestDto request);

        PaginationResponseDto<UserResponseDto> getAllUsers(PaginationRequestDto request);

        UserResponseDto getUserById(UUID userId);

        UserResponseDto createUser(CreateUserRequestDto request);

        UserResponseDto updateUser(UUID userId, UpdateUserRequestDto request);

        CommonResponseDto deleteUserAccount(UUID userId);

        PaginationResponseDto<UserResponseDto> searchUserByUsername(String searchSentence,
                        PaginationRequestDto paginationRequestDto);

        PaginationResponseDto<UserResponseDto> searchUserByEmail(String searchSentence,
                        PaginationRequestDto paginationRequestDto);

        PaginationResponseDto<UserResponseDto> searchUserByPhone(String searchSentence,
                        PaginationRequestDto paginationRequestDto);
}
