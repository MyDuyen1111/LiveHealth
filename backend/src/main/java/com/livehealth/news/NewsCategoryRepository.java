package com.livehealth.news;

import com.livehealth.news.dto.response.category.NewsCategoryCountDto;
import com.livehealth.shared.base.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class NewsCategoryRepository extends BaseRepository<NewsCategory, UUID> {

    public NewsCategoryRepository() {
        super(NewsCategory.class);
    }

    public boolean existsByName(String name) {
        Long count = em.createQuery("SELECT COUNT(c) FROM NewsCategory c WHERE c.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    public List<NewsCategoryCountDto> findNewsCategoryCounts() {
        return em.createQuery("SELECT new com.livehealth.news.dto.response.category.NewsCategoryCountDto(c.id, c.name, COUNT(n)) " +
                "FROM NewsCategory c LEFT JOIN c.newsList n " +
                "GROUP BY c.id, c.name " +
                "ORDER BY COUNT(n) DESC", NewsCategoryCountDto.class)
                .getResultList();
    }
}
