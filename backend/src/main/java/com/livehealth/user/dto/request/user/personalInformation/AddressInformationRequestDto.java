package com.livehealth.user.dto.request.user.personalInformation;

import com.livehealth.shared.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressInformationRequestDto {

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String companyName;

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String streetAddress;

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String country;

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String state;

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    int zipCode;
}
