package com.livehealth.shared.web;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "client_testimonial")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientTestimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    UUID id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String content;

    @Column(nullable = false)
    String position;

    @Column(nullable = false)
    String avatarUrl;

    @Column(nullable = false)
    LocalDateTime createdAt;
}
