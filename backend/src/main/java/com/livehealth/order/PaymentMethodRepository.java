package com.livehealth.order;

import com.livehealth.cart.PaymentMethod;
import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PaymentMethodRepository extends BaseRepository<PaymentMethod, UUID> {

    public PaymentMethodRepository() {
        super(PaymentMethod.class);
    }

    public boolean existsByName(String name) {
        Long count = em.createQuery("SELECT COUNT(p) FROM PaymentMethod p WHERE p.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    public Optional<PaymentMethod> findFirstByName(String name) {
        return em.createQuery("SELECT p FROM PaymentMethod p WHERE p.name = :name", PaymentMethod.class)
                .setParameter("name", name)
                .getResultList().stream()
                .findFirst();
    }
}
