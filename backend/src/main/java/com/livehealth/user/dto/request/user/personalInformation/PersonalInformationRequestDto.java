package com.livehealth.user.dto.request.user.personalInformation;

import com.livehealth.shared.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonalInformationRequestDto {

    @Schema(description = "Số điện thoại", example = "0123456789")
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String phone;

    @Schema(description = "Địa chỉ")
    @Valid
    @NotNull(message = ErrorMessage.NOT_BLANK_FIELD)
    AddressInformationRequestDto address;

}
