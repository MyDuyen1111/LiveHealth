package com.livehealth.auth.dto.request;

import com.livehealth.shared.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequestDto {

    @Schema(description = "Email", example = "[EMAIL_ADDRESS]")
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String email;

    @Schema(description = "Mật khẩu", example = "User123@")
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String password;

}
