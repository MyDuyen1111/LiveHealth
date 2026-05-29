package com.livehealth.order;

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
public class OrderRepository extends BaseRepository<Order, UUID> {

    public OrderRepository() {
        super(Order.class);
    }

    public Integer findMaxOrderNumber() {
        return em.createQuery("SELECT MAX(CAST(SUBSTRING(o.orderNumber, 2) AS int)) FROM Order o", Integer.class)
                .getSingleResult();
    }

    public Page<Order> findByUserId(UUID userId, Pageable pageable) {
        String countJpql = "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId";
        long total = em.createQuery(countJpql, Long.class)
                .setParameter("userId", userId)
                .getSingleResult();

        StringBuilder jpql = new StringBuilder("SELECT o FROM Order o WHERE o.user.id = :userId");
        if (pageable != null && pageable.getSort() != null && pageable.getSort().isSorted()) {
            jpql.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : pageable.getSort().getOrders()) {
                if (!first) jpql.append(", ");
                jpql.append("o.").append(order.getProperty()).append(" ").append(order.getDirection().name());
                first = false;
            }
        }

        var query = em.createQuery(jpql.toString(), Order.class)
                .setParameter("userId", userId);

        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<Order> list = query.getResultList();
        return new PageImpl<>(list, pageable != null ? pageable : PageRequest.of(0, list.isEmpty() ? 1 : list.size()), total);
    }

    public Double calculateTotalRevenue() {
        Double revenue = em.createQuery("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status != :status", Double.class)
                .setParameter("status", OrderStatus.CANCELLED)
                .getSingleResult();
        return revenue != null ? revenue : 0.0;
    }

    public List<Order> findRecentOrders(int limit) {
        return em.createQuery("SELECT o FROM Order o ORDER BY o.orderDate DESC", Order.class)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Order> findByUserId(UUID userId) {
        return em.createQuery("SELECT o FROM Order o WHERE o.user.id = :userId", Order.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
