package com.livehealth.order;

import com.livehealth.cart.ShippingMethod;
import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ShippingMethodRepository extends BaseRepository<ShippingMethod, UUID> {

    public ShippingMethodRepository() {
        super(ShippingMethod.class);
    }

    public boolean existsByName(String name) {
        Long count = em.createQuery("SELECT COUNT(s) FROM ShippingMethod s WHERE s.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    public Optional<ShippingMethod> findFirstByName(String name) {
        return em.createQuery("SELECT s FROM ShippingMethod s WHERE s.name = :name", ShippingMethod.class)
                .setParameter("name", name)
                .getResultList().stream()
                .findFirst();
    }
}
