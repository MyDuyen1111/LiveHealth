package com.livehealth.product;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ProductPromotionRepository extends BaseRepository<ProductPromotion, UUID> {

    public ProductPromotionRepository() {
        super(ProductPromotion.class);
    }

    public List<ProductPromotion> findByProductId(UUID productId) {
        return em.createQuery("SELECT p FROM ProductPromotion p WHERE p.product.id = :productId", ProductPromotion.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    public Optional<ProductPromotion> findByProductIdAndIsActiveTrue(UUID productId) {
        return em.createQuery("SELECT p FROM ProductPromotion p WHERE p.product.id = :productId AND p.isActive = true", ProductPromotion.class)
                .setParameter("productId", productId)
                .getResultList().stream()
                .findFirst();
    }
}
