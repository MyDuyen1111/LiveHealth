package com.livehealth.shared.web.dto.request.webinfo;

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
public class WebInformationRequestDto {

    Integer yearOfHardWork;

    Integer happyCustomers;

    Integer qualifiedTeamMembers;

    Integer monthlyOrders;

    String companyName;

    String slogan;

    String phone;

    String email;

    String address;

    String copyrightText;
}
