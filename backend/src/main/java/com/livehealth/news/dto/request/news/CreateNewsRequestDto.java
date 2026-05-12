package com.livehealth.news.dto.request.news;

import jakarta.validation.constraints.NotBlank;
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
public class CreateNewsRequestDto {

    @NotBlank(message = "Tiêu đề không được để trống")
    String title;

    @NotBlank(message = "Nội dung không được để trống")
    String content;

    UUID categoryId;

    List<UUID> tagIds;
}
