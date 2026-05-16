package com.livehealth.order.dto.request.order;

import com.livehealth.order.OrderStatus;
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
public class UpdateOrderStatusRequestDto {

    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    OrderStatus status;
}
