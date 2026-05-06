package com.livehealth.user;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.livehealth.shared.constant.*;
import com.livehealth.shared.dto.pagination.*;
import com.livehealth.user.dto.request.admin.*;
import com.livehealth.user.dto.request.user.personalInformation.PersonalInformationRequestDto;
import com.livehealth.user.dto.request.user.personalInformation.UpdatePersonalInformationRequestDto;
import com.livehealth.user.dto.request.user.profile.UpdateProfileRequestDto;
import com.livehealth.user.dto.request.user.profile.ChangePasswordRequestDto;
import com.livehealth.shared.dto.CommonResponseDto;
import com.livehealth.user.dto.response.user.*;
import com.livehealth.user.*;
import com.livehealth.user.UserMapper;
import com.livehealth.shared.exception.*;
import com.livehealth.shared.helper.PersonalInformationHelper;
import com.livehealth.user.*;
import com.livehealth.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import com.livehealth.shared.base.MultipartFile;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    AddressRepository addressRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    PersonalInformationHelper stringPersonalInformationHelper;

    Cloudinary cloudinary;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserResponseDto personalInformation(UUID userId, PersonalInformationRequestDto request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(
                        HttpStatus.UNAUTHORIZED,
                        ErrorMessage.User.ERR_USER_NOT_EXISTED));

        if (userRepository.existsUserByPhone(request.getPhone()))
            throw new VsException(HttpStatus.CONFLICT, ErrorMessage.User.ERR_PHONE_EXISTED);

        UUID oldAddressId = null;
        if (user.getAddress() != null) {
            oldAddressId = user.getAddress().getId();
        }

        Address address = findOrCreateAddress(
                request.getAddress().getCountry(),
                request.getAddress().getState(),
                request.getAddress().getStreetAddress(),
                request.getAddress().getCompanyName(),
                request.getAddress().getZipCode());

        user.setPhone(request.getPhone());
        user.setAddress(address);

        userRepository.save(user);

        if (oldAddressId != null && !oldAddressId.equals(address.getId())) {
            long userCountWithOldAddress = userRepository.countByAddressId(oldAddressId);
            if (userCountWithOldAddress == 0) {
                addressRepository.deleteById(oldAddressId);
            }
        }

        return userMapper.userToUserResponseDto(user);
    }

    private Address findOrCreateAddress(String country, String state, String streetAddress, String companyName,
            int zipCode) {
        return addressRepository
                .findByCountryAndStateAndStreetAddressAndCompanyNameAndZipCode(country, state, streetAddress,
                        companyName, zipCode)
                .orElseGet(() -> {
                    Address newAddress = new Address();
                    newAddress.setCountry(country);
                    newAddress.setState(state);
                    newAddress.setStreetAddress(streetAddress);
                    newAddress.setCompanyName(companyName);
                    newAddress.setZipCode(zipCode);
                    return addressRepository.save(newAddress);
                });
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserResponseDto uploadAvatar(UUID userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(
                        HttpStatus.UNAUTHORIZED,
                        ErrorMessage.User.ERR_USER_NOT_EXISTED));

        if (user.getAvatarPublicId() != null) {
            try {
                cloudinary.uploader().destroy(user.getAvatarPublicId(), ObjectUtils.emptyMap());
            } catch (IOException e) {
                throw new VsException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.ERR_UPLOAD_IMAGE_FAIL);
            }
        }

        String imageUrl;
        String publicId;
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            imageUrl = (String) result.get("secure_url");
            publicId = (String) result.get("public_id");
        } catch (Exception e) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.ERR_UPLOAD_IMAGE_FAIL);
        }

        user.setLinkAvatar(imageUrl);
        user.setAvatarPublicId(publicId);

        userRepository.save(user);

        return userMapper.userToUserResponseDto(user);
    }

    @Override
    public PaginationResponseDto<UserResponseDto> getAllUsers(PaginationRequestDto request) {

        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize());

        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponseDto> userResponseDtos = userPage.getContent()
                .stream()
                .map(userMapper::userToUserResponseDto)
                .collect(Collectors.toList());

        PagingMeta pagingMeta = new PagingMeta(
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                request.getPageNum() + 1,
                request.getPageSize(),
                null,
                null);

        return new PaginationResponseDto<>(pagingMeta, userResponseDtos);
    }

    @Override
    public UserResponseDto getUserById(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_USER_NOT_EXISTED));

        return userMapper.userToUserResponseDto(user);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserResponseDto createUser(CreateUserRequestDto request) {

        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_EMAIL_EXISTED);
        }
        if (userRepository.existsUserByPhone(request.getPhone())) {
            throw new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_PHONE_EXISTED);
        }

        User user = userMapper.createUserRequestDtoToUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(com.livehealth.shared.utils.TimeUtil.today());

        return userMapper.userToUserResponseDto(userRepository.save(user));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserResponseDto updateUser(UUID userId, UpdateUserRequestDto request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_USER_NOT_EXISTED));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsUserByEmail(request.getEmail())) {
                throw new VsException(HttpStatus.CONFLICT, ErrorMessage.User.ERR_EMAIL_EXISTED);
            }
        }

        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsUserByPhone(request.getPhone())) {
                throw new VsException(HttpStatus.CONFLICT, ErrorMessage.User.ERR_PHONE_EXISTED);
            }
        }

        userMapper.updateUserFromDto(request, user);

        User updatedUser = userRepository.save(user);

        return userMapper.userToUserResponseDto(updatedUser);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResponseDto deleteUserAccount(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(HttpStatus.BAD_REQUEST, ErrorMessage.User.ERR_USER_NOT_EXISTED));

        UUID addressId = null;

        if (user.getAddress() != null) {
            addressId = user.getAddress().getId();
        }

        userRepository.delete(user);

        if (addressId != null) {
            long userCountWithAddress = userRepository.countByAddressId(addressId);

            if (userCountWithAddress == 0) {
                addressRepository.deleteById(addressId);
            }
        }

        return new CommonResponseDto(HttpStatus.OK, SuccessMessage.User.DELETE_SUCCESS);
    }

    @Override
    public UserResponseDto getMyProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(
                        HttpStatus.UNAUTHORIZED,
                        ErrorMessage.User.ERR_USER_NOT_EXISTED));

        UserResponseDto userResponseDto = userMapper.userToUserResponseDto(user);

        return userResponseDto;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserResponseDto updateProfile(UpdateProfileRequestDto request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(
                        HttpStatus.UNAUTHORIZED,
                        ErrorMessage.User.ERR_USER_NOT_EXISTED));

        if (request.getPersonalInformation() != null) {

            UpdatePersonalInformationRequestDto personalInfo = stringPersonalInformationHelper.handleEmptyStrings(
                    request.getPersonalInformation());

            if (personalInfo.getPhone() != null && !personalInfo.getPhone().equals(user.getPhone())) {
                if (userRepository.existsUserByPhone(personalInfo.getPhone())) {
                    throw new VsException(HttpStatus.CONFLICT, ErrorMessage.User.ERR_PHONE_EXISTED);
                }
            }

            userMapper.updateUserFromPersonalInformationDto(personalInfo, user);

            if (personalInfo.getAddress() != null) {
                UUID oldAddressId = null;
                if (user.getAddress() != null) {
                    oldAddressId = user.getAddress().getId();
                }

                Address address = findOrCreateAddress(
                        personalInfo.getAddress().getCountry(),
                        personalInfo.getAddress().getState(),
                        personalInfo.getAddress().getStreetAddress(),
                        personalInfo.getAddress().getCompanyName(),
                        personalInfo.getAddress().getZipCode());

                user.setAddress(address);

                if (oldAddressId != null && !oldAddressId.equals(address.getId())) {
                    long userCountWithOldAddress = userRepository.countByAddressId(oldAddressId);
                    if (userCountWithOldAddress == 0) {
                        addressRepository.deleteById(oldAddressId);
                    }
                }
            }
        }

        User updatedUser = userRepository.save(user);

        UserResponseDto userResponseDto = userMapper.userToUserResponseDto(updatedUser);

        return userResponseDto;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserResponseDto updateBillingAddress(UUID userId, com.livehealth.order.dto.request.order.AddressDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(
                        HttpStatus.UNAUTHORIZED,
                        ErrorMessage.User.ERR_USER_NOT_EXISTED));

        UUID oldAddressId = null;
        if (user.getAddress() != null) {
            oldAddressId = user.getAddress().getId();
        }

        Address address = findOrCreateAddress(
                request.getCountry(),
                request.getState(),
                request.getStreetAddress(),
                request.getCompanyName(),
                request.getZipCode());

        user.setAddress(address);
        userRepository.save(user);

        if (oldAddressId != null && !oldAddressId.equals(address.getId())) {
            long userCountWithOldAddress = userRepository.countByAddressId(oldAddressId);
            if (userCountWithOldAddress == 0) {
                addressRepository.deleteById(oldAddressId);
            }
        }

        return userMapper.userToUserResponseDto(user);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CommonResponseDto changePassword(UUID userId, ChangePasswordRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VsException(
                        HttpStatus.UNAUTHORIZED,
                        ErrorMessage.User.ERR_USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new VsException(HttpStatus.BAD_REQUEST, "Mật khẩu hiện tại không chính xác");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new VsException(HttpStatus.BAD_REQUEST, "Mật khẩu mới và mật khẩu xác nhận không khớp");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new CommonResponseDto(HttpStatus.OK, "Đổi mật khẩu thành công!");
    }

    @Override
    public PaginationResponseDto<UserResponseDto> searchUserByUsername(
            String searchSentence,
            PaginationRequestDto paginationRequestDto) {

        Pageable pageable = PageRequest.of(
                paginationRequestDto.getPageNum(),
                paginationRequestDto.getPageSize());

        Page<User> userPage = userRepository.findUserByEmailContainingIgnoreCase(
                searchSentence,
                pageable);

        return searchUsers(userPage, paginationRequestDto);
    }

    @Override
    public PaginationResponseDto<UserResponseDto> searchUserByEmail(
            String searchSentence,
            PaginationRequestDto paginationRequestDto) {

        Pageable pageable = PageRequest.of(
                paginationRequestDto.getPageNum(),
                paginationRequestDto.getPageSize());

        Page<User> userPage = userRepository.findUserByEmailContainingIgnoreCase(
                searchSentence,
                pageable);

        return searchUsers(userPage, paginationRequestDto);
    }

    @Override
    public PaginationResponseDto<UserResponseDto> searchUserByPhone(
            String searchSentence,
            PaginationRequestDto paginationRequestDto) {

        Pageable pageable = PageRequest.of(
                paginationRequestDto.getPageNum(),
                paginationRequestDto.getPageSize());

        Page<User> userPage = userRepository.findUserByPhoneContaining(
                searchSentence,
                pageable);

        return searchUsers(userPage, paginationRequestDto);
    }

    private PaginationResponseDto<UserResponseDto> searchUsers(
            Page<User> userPage,
            PaginationRequestDto paginationRequestDto) {

        List<UserResponseDto> result = userPage.getContent()
                .stream()
                .map(userMapper::userToUserResponseDto)
                .filter(Objects::nonNull)
                .toList();
        PagingMeta pagingMeta = new PagingMeta(
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                paginationRequestDto.getPageNum() + 1,
                paginationRequestDto.getPageSize(),
                null, null);
        return new PaginationResponseDto<>(pagingMeta, result);
    }
}
