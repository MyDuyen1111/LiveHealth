package com.livehealth.cart;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CartItemRepository extends BaseRepository<CartItem, UUID> {

    public CartItemRepository() {
        super(CartItem.class);
    }

    public Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId) {
        return em.createQuery(
            "SELECT c FROM CartItem c WHERE c.cart.id = :cartId AND c.product.id = :productId",
            CartItem.class
        )
        .setParameter("cartId", cartId)
        .setParameter("productId", productId)
        .getResultList().stream()
        .findFirst();
    }
}
