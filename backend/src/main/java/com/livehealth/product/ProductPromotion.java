package com.livehealth.product;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_promotion")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    UUID id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    double discountValue;

    @Column(nullable = false)
    String discountType;

    @Column(nullable = false)
    LocalDateTime startDate;

    @Column(nullable = false)
    LocalDateTime endDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Builder.Default
    @Column(nullable = false)
    boolean isActive = true;

    @Column(nullable = false)
    LocalDateTime createdAt;
}
