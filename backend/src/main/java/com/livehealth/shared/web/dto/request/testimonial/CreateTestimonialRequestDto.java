package com.livehealth.shared.web.dto.request.testimonial;

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
public class CreateTestimonialRequestDto {

    @NotBlank(message = "Tên khách hàng không được để trống")
    String name;

    @NotBlank(message = "Nội dung không được để trống")
    String content;

    @NotBlank(message = "Vị trí/Chức vụ không được để trống")
    String position;
}
