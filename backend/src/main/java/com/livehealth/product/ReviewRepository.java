package com.livehealth.product;

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
public class ReviewRepository extends BaseRepository<Review, UUID> {

    public ReviewRepository() {
        super(Review.class);
    }

    public Page<Review> findByProductId(UUID productId, Pageable pageable) {
        String countJpql = "SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId";
        long total = em.createQuery(countJpql, Long.class)
                .setParameter("productId", productId)
                .getSingleResult();

        StringBuilder jpql = new StringBuilder("SELECT r FROM Review r WHERE r.product.id = :productId");
        if (pageable != null && pageable.getSort() != null && pageable.getSort().isSorted()) {
            jpql.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : pageable.getSort().getOrders()) {
                if (!first) jpql.append(", ");
                jpql.append("r.").append(order.getProperty()).append(" ").append(order.getDirection().name());
                first = false;
            }
        }

        var query = em.createQuery(jpql.toString(), Review.class)
                .setParameter("productId", productId);

        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<Review> list = query.getResultList();
        return new PageImpl<>(list, pageable != null ? pageable : PageRequest.of(0, list.isEmpty() ? 1 : list.size()), total);
    }
}
