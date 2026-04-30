package com.livehealth.shared.web.dto.response.webinfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebInformationResponseDto {

    UUID id;

    String logoUrl;

    int yearOfHardWork;

    int happyCustomers;

    int qualifiedTeamMembers;

    int monthlyOrders;

    String companyName;

    String slogan;

    String phone;

    String email;

    String address;

    String copyrightText;
}
