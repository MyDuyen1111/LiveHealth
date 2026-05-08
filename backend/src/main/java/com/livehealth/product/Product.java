package com.livehealth.product;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    UUID id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    double oldPrice;

    @Column(nullable = false)
    int stock;

    @ElementCollection
    @CollectionTable(name = "product_image", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", nullable = false)
    List<String> imageUrl;

    @Column(nullable = false)
    String sku;

    @Column(nullable = false)
    String createdAt;

    @Column(nullable = false)
    String updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_tag", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    List<Tag> tags;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Review> reviews;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    ProductPromotion promotion; // Sale and Deal

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductAttribute> additionalInfo; // Additional info
}
