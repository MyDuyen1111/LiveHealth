package com.livehealth.shared.dto;

import lombok.*;
import com.livehealth.shared.base.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CommonResponseDto {

    private HttpStatus status;

    private String message;

}
