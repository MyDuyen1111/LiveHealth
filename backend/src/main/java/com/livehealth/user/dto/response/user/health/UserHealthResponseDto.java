package com.livehealth.user.dto.response.user.health;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserHealthResponseDto {

    UUID id;

    UUID userId;

    String gender;

    int height;

    int weight;

    int age;

    double basalMetabolicRate;

    int maximumHeartRate;

    double TDEE;

}
