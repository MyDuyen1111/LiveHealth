package com.livehealth.cart.dto.request.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
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
public class ShippingMethodRequestDto {

    @NotBlank(message = "Tên phương thức giao hàng không được để trống")
    String name;

    @PositiveOrZero(message = "Giá phương thức giao hàng không hợp lệ")
    double price;

    String description;

    boolean isActive = true;
}
