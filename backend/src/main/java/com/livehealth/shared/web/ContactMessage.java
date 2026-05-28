package com.livehealth.shared.web;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contact_message")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    UUID id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String email;

    @Column(nullable = false)
    String subject;

    @Column(nullable = false, length = 2000)
    String message;

    @Column(length = 2000)
    String reply;

    @Column
    LocalDateTime repliedAt;

    @Column(nullable = false)
    String status; // "PENDING", "REPLIED"

    @Column(nullable = false)
    LocalDateTime createdAt;
}
