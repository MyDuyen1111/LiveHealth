package com.livehealth.cart.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRequestDto {

    @NotNull(message = "Product ID không được để trống")
    UUID productId;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    int quantity = 1;
}
