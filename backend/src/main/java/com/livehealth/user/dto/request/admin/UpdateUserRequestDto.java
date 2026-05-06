package com.livehealth.user.dto.request.admin;


import com.livehealth.user.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequestDto {


    @Schema(description = "Email người dùng", example = "user@gmail.com")
    String email;

    @Schema(description = "Họ/Tên đệm", example = "Phạm Văn")
    String firstName;

    @Schema(description = "Tên", example = "A")
    String lastName;


    @Schema(description = "Số điện thoại", example = "0123456789")
    String phone;


    @Schema(description = "Vai trò", example = "USER")
    Role role;
    Boolean isActive;
}
