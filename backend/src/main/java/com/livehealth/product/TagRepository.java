package com.livehealth.product;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TagRepository extends BaseRepository<Tag, UUID> {

    public TagRepository() {
        super(Tag.class);
    }

    public boolean existsByName(String name) {
        Long count = em.createQuery("SELECT COUNT(t) FROM Tag t WHERE t.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    public Optional<Tag> findByName(String name) {
        return em.createQuery("SELECT t FROM Tag t WHERE t.name = :name", Tag.class)
                .setParameter("name", name)
                .getResultList().stream()
                .findFirst();
    }
}
