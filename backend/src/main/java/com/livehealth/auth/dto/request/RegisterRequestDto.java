package com.livehealth.auth.dto.request;

import com.livehealth.shared.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequestDto {

    @Schema(description = "Họ/Tên đệm", example = "Nguyễn")
    @NotBlank(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    String firstName;

    @Schema(description = "Tên", example = "Duyên")
    @NotBlank(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    String lastName;

    @Schema(description = "Email người dùng", example = "user@gmail.com")
    @NotBlank(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    @Email(message = ErrorMessage.INVALID_FORMAT_SOME_THING_FIELD)
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$", message = ErrorMessage.INVALID_FORMAT_SOME_THING_FIELD)
    String email;

    @Schema(description = "Mật khẩu", example = "User123@")
    @NotBlank(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$", message = ErrorMessage.INVALID_FORMAT_PASSWORD)
    String password;

    @Schema(description = "Xác nhận mật khẩu", example = "User123@")
    @NotBlank(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    String reEnterPassword;

    @Schema(description = "Số điện thoại", example = "0901234567")
    String phone;

    @Schema(description = "Đồng ý điều khoản", example = "true")
    @AssertTrue(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    boolean acceptedTerms;
}
