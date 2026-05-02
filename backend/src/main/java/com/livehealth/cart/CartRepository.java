package com.livehealth.cart;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CartRepository extends BaseRepository<Cart, UUID> {

    public CartRepository() {
        super(Cart.class);
    }

    public Optional<Cart> findByUserId(UUID userId) {
        return em.createQuery("SELECT c FROM Cart c WHERE c.user.id = :userId", Cart.class)
                .setParameter("userId", userId)
                .getResultList().stream()
                .findFirst();
    }
}
