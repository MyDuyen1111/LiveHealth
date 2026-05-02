package com.livehealth.news;

import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class NewsTagRepository extends BaseRepository<NewsTag, UUID> {

    public NewsTagRepository() {
        super(NewsTag.class);
    }

    public boolean existsByName(String name) {
        Long count = em.createQuery("SELECT COUNT(t) FROM NewsTag t WHERE t.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }
}
