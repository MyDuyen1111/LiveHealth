package com.livehealth.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "address")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    UUID id;

    @Nationalized
    String companyName;

    @Nationalized
    @Column(nullable = false)
    String streetAddress;

    @Column(nullable = false)
    String country;

    @Column(nullable = false)
    String state;

    @Column(nullable = false)
    int zipCode;
}
