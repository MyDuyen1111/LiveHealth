package com.livehealth.product;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class BrandRepository extends BaseRepository<Brand, UUID> {

    public BrandRepository() {
        super(Brand.class);
    }

    public boolean existsByName(String name) {
        Long count = em.createQuery("SELECT COUNT(b) FROM Brand b WHERE b.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    public Optional<Brand> findByName(String name) {
        return em.createQuery("SELECT b FROM Brand b WHERE b.name = :name", Brand.class)
                .setParameter("name", name)
                .getResultList().stream()
                .findFirst();
    }
}
