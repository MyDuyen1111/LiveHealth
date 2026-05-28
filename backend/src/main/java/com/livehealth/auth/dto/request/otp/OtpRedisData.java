package com.livehealth.auth.dto.request.otp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpRedisData implements Serializable {
    private String otp;
    private String data;
}
