package com.livehealth.order.dto.request.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {

    String companyName;

    @NotBlank(message = "Địa chỉ cụ thể không được để trống")
    String streetAddress;

    @NotBlank(message = "Quốc gia không được để trống")
    String country;

    @NotBlank(message = "Bang/Tỉnh không được để trống")
    String state;

    @NotNull(message = "Mã bưu chính không được để trống")
    int zipCode;
}
