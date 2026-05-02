package com.livehealth.news;

import com.livehealth.shared.base.BaseRepository;
import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageImpl;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.pagination.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class NewsRepository extends BaseRepository<News, UUID> {

    public NewsRepository() {
        super(News.class);
    }

    public Page<News> findNewsWithFilters(UUID categoryId, UUID tagId, Pageable pageable) {
        String countJpql = "SELECT COUNT(DISTINCT n) FROM News n LEFT JOIN n.tags t WHERE " +
                           "(:categoryId IS NULL OR n.category.id = :categoryId) AND " +
                           "(:tagId IS NULL OR t.id = :tagId)";
        long total = em.createQuery(countJpql, Long.class)
                .setParameter("categoryId", categoryId)
                .setParameter("tagId", tagId)
                .getSingleResult();

        StringBuilder jpql = new StringBuilder("SELECT DISTINCT n FROM News n LEFT JOIN n.tags t WHERE " +
                                               "(:categoryId IS NULL OR n.category.id = :categoryId) AND " +
                                               "(:tagId IS NULL OR t.id = :tagId)");

        if (pageable != null && pageable.getSort() != null && pageable.getSort().isSorted()) {
            jpql.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : pageable.getSort().getOrders()) {
                if (!first) jpql.append(", ");
                jpql.append("n.").append(order.getProperty()).append(" ").append(order.getDirection().name());
                first = false;
            }
        }

        var query = em.createQuery(jpql.toString(), News.class)
                .setParameter("categoryId", categoryId)
                .setParameter("tagId", tagId);

        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<News> list = query.getResultList();
        return new PageImpl<>(list, pageable != null ? pageable : PageRequest.of(0, list.isEmpty() ? 1 : list.size()), total);
    }
}
