package com.livehealth.product;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProductAttributeRepository extends BaseRepository<ProductAttribute, UUID> {

    public ProductAttributeRepository() {
        super(ProductAttribute.class);
    }

    public List<ProductAttribute> findByProductId(UUID productId) {
        return em.createQuery("SELECT a FROM ProductAttribute a WHERE a.product.id = :productId", ProductAttribute.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    public boolean existsByProductIdAndAttributeKey(UUID productId, String attributeKey) {
        Long count = em.createQuery("SELECT COUNT(a) FROM ProductAttribute a WHERE a.product.id = :productId AND a.attributeKey = :attributeKey", Long.class)
                .setParameter("productId", productId)
                .setParameter("attributeKey", attributeKey)
                .getSingleResult();
        return count > 0;
    }
}
