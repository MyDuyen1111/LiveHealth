package com.livehealth.user.dto.request.user.personalInformation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePersonalInformationRequestDto {

    @Schema(description = "Họ", example = "Nguyễn")
    String firstName;

    @Schema(description = "Tên", example = "Văn A")
    String lastName;

    @Schema(description = "Số điện thoại", example = "0123456789")
    String phone;

    @Schema(description = "Địa chỉ")
    @Valid
    UpdateAddressRequestDto address;
}
