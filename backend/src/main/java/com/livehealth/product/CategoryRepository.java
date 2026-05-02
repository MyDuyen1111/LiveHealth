package com.livehealth.product;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CategoryRepository extends BaseRepository<Category, UUID> {

    public CategoryRepository() {
        super(Category.class);
    }

    public boolean existsByName(String name) {
        Long count = em.createQuery("SELECT COUNT(c) FROM Category c WHERE c.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    public Optional<Category> findByName(String name) {
        return em.createQuery("SELECT c FROM Category c WHERE c.name = :name", Category.class)
                .setParameter("name", name)
                .getResultList().stream()
                .findFirst();
    }
}
