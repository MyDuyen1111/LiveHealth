package com.livehealth.shared.web;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "web_information")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    UUID id;

    @Column(nullable = false)
    String logoUrl;

    @Column(nullable = false)
    int yearOfHardWork;

    @Column(nullable = false)
    int happyCustomers;

    @Column(nullable = false)
    int qualifiedTeamMembers;

    @Column(nullable = false)
    int monthlyOrders;

    @Column(nullable = false)
    String companyName;

    @Column(nullable = false)
    String slogan;

    @Column(nullable = false)
    String phone;

    @Column(nullable = false)
    String email;

    @Column(nullable = false)
    String address;

    @Column(nullable = false)
    String copyrightText;
}
