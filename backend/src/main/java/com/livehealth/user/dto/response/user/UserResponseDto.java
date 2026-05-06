package com.livehealth.user.dto.response.user;

import com.livehealth.user.Address;
import com.livehealth.user.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDto {
    UUID id;

    String email;

    String firstName;

    String lastName;

    String phone;
    String linkAvatar;

    LocalDate createdAt;

    Role role;

    Address address;
}
