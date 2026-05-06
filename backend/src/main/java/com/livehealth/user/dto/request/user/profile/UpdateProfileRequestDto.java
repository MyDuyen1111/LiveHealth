package com.livehealth.user.dto.request.user.profile;

import com.livehealth.user.dto.request.user.personalInformation.UpdatePersonalInformationRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequestDto {

    @Schema(description = "Thông tin cá nhân (bao gồm thông tin địa chỉ)")
    @Valid
    UpdatePersonalInformationRequestDto personalInformation;
}
