package com.livehealth.shared.web.dto.request.member;

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
public class CreateMemberRequestDto {

    @NotBlank(message = "Tên thành viên không được để trống")
    String name;

    @NotBlank(message = "Chức vụ không được để trống")
    String position;
}
