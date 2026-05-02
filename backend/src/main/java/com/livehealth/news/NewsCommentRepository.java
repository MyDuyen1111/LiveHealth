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
public class NewsCommentRepository extends BaseRepository<NewsComment, UUID> {

    public NewsCommentRepository() {
        super(NewsComment.class);
    }

    public Page<NewsComment> findByNewsId(UUID newsId, Pageable pageable) {
        String countJpql = "SELECT COUNT(c) FROM NewsComment c WHERE c.news.id = :newsId";
        long total = em.createQuery(countJpql, Long.class)
                .setParameter("newsId", newsId)
                .getSingleResult();

        StringBuilder jpql = new StringBuilder("SELECT c FROM NewsComment c WHERE c.news.id = :newsId");
        if (pageable != null && pageable.getSort() != null && pageable.getSort().isSorted()) {
            jpql.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : pageable.getSort().getOrders()) {
                if (!first) jpql.append(", ");
                jpql.append("c.").append(order.getProperty()).append(" ").append(order.getDirection().name());
                first = false;
            }
        }

        var query = em.createQuery(jpql.toString(), NewsComment.class)
                .setParameter("newsId", newsId);

        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<NewsComment> list = query.getResultList();
        return new PageImpl<>(list, pageable != null ? pageable : PageRequest.of(0, list.isEmpty() ? 1 : list.size()), total);
    }
}
