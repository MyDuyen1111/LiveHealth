package com.livehealth.cart.dto.request.cart;

import jakarta.validation.constraints.NotBlank;
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
public class PaymentMethodRequestDto {

    @NotBlank(message = "Tên phương thức thanh toán không được để trống")
    String name;

    boolean isActive = true;
}
