package com.livehealth.product.dto.request.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateProductRequestDto {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    String name;

    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    String description;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Min(value = 0, message = "Giá sản phẩm phải lớn hơn hoặc bằng 0")
    Double oldPrice;

    @NotNull(message = "Số lượng sản phẩm không được để trống")
    @Min(value = 0, message = "Số lượng sản phẩm phải lớn hơn hoặc bằng 0")
    Integer stock;

    @NotBlank(message = "SKU không được để trống")
    String sku;

    @NotNull(message = "Brand ID không được để trống")
    UUID brandId;

    @NotNull(message = "Category ID không được để trống")
    UUID categoryId;

    List<UUID> tagIds;
}
