package com.livehealth.user.dto.request.user.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequestDto {

    @NotBlank(message = "invalid.general.not-blank")
    String oldPassword;

    @NotBlank(message = "invalid.general.not-blank")
    String newPassword;

    @NotBlank(message = "invalid.general.not-blank")
    String confirmPassword;
}
