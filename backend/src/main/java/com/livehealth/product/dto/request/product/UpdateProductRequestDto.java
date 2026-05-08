package com.livehealth.product.dto.request.product;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductRequestDto {

    String name;

    String description;

    @Min(value = 0, message = "Giá sản phẩm phải lớn hơn hoặc bằng 0")
    Double oldPrice;

    @Min(value = 0, message = "Số lượng sản phẩm phải lớn hơn hoặc bằng 0")
    Integer stock;

    String sku;

    UUID brandId;

    UUID categoryId;

    List<UUID> tagIds;
}
