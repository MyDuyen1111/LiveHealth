package com.livehealth.auth.dto.response;

import com.livehealth.shared.constant.CommonConstant;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.livehealth.shared.base.HttpStatus;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LoginResponseDto {

    HttpStatus status;

    String message;

    String accessToken;

    String refreshToken;

    UUID id;

    @Builder.Default
    String tokenType = CommonConstant.BEARER_TOKEN;

}