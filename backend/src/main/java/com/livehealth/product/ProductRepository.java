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
public class ProductRepository extends BaseRepository<Product, UUID> {

    public ProductRepository() {
        super(Product.class);
    }

    public Page<Product> findByCategoryId(UUID categoryId, Pageable pageable) {
        String countJpql = "SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId";
        long total = em.createQuery(countJpql, Long.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();

        StringBuilder jpql = new StringBuilder("SELECT p FROM Product p WHERE p.category.id = :categoryId");
        if (pageable != null && pageable.getSort() != null && pageable.getSort().isSorted()) {
            jpql.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : pageable.getSort().getOrders()) {
                if (!first) jpql.append(", ");
                jpql.append("p.").append(order.getProperty()).append(" ").append(order.getDirection().name());
                first = false;
            }
        }

        var query = em.createQuery(jpql.toString(), Product.class)
                .setParameter("categoryId", categoryId);

        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<Product> list = query.getResultList();
        return new PageImpl<>(list, pageable != null ? pageable : PageRequest.of(0, list.isEmpty() ? 1 : list.size()), total);
    }

    public Page<Product> findByBrandId(UUID brandId, Pageable pageable) {
        String countJpql = "SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId";
        long total = em.createQuery(countJpql, Long.class)
                .setParameter("brandId", brandId)
                .getSingleResult();

        StringBuilder jpql = new StringBuilder("SELECT p FROM Product p WHERE p.brand.id = :brandId");
        if (pageable != null && pageable.getSort() != null && pageable.getSort().isSorted()) {
            jpql.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : pageable.getSort().getOrders()) {
                if (!first) jpql.append(", ");
                jpql.append("p.").append(order.getProperty()).append(" ").append(order.getDirection().name());
                first = false;
            }
        }

        var query = em.createQuery(jpql.toString(), Product.class)
                .setParameter("brandId", brandId);

        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<Product> list = query.getResultList();
        return new PageImpl<>(list, pageable != null ? pageable : PageRequest.of(0, list.isEmpty() ? 1 : list.size()), total);
    }

    public Page<Product> findWithFilters(UUID categoryId, UUID brandId, String keyword, Pageable pageable) {
        String countJpql = "SELECT COUNT(p) FROM Product p WHERE " +
                           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
                           "(:brandId    IS NULL OR p.brand.id    = :brandId)    AND " +
                           "(:keyword    IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))";
        long total = em.createQuery(countJpql, Long.class)
                .setParameter("categoryId", categoryId)
                .setParameter("brandId", brandId)
                .setParameter("keyword", keyword)
                .getSingleResult();

        StringBuilder jpql = new StringBuilder("SELECT p FROM Product p WHERE " +
                                               "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
                                               "(:brandId    IS NULL OR p.brand.id    = :brandId)    AND " +
                                               "(:keyword    IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))");
        if (pageable != null && pageable.getSort() != null && pageable.getSort().isSorted()) {
            jpql.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : pageable.getSort().getOrders()) {
                if (!first) jpql.append(", ");
                jpql.append("p.").append(order.getProperty()).append(" ").append(order.getDirection().name());
                first = false;
            }
        }

        var query = em.createQuery(jpql.toString(), Product.class)
                .setParameter("categoryId", categoryId)
                .setParameter("brandId", brandId)
                .setParameter("keyword", keyword);

        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<Product> list = query.getResultList();
        return new PageImpl<>(list, pageable != null ? pageable : PageRequest.of(0, list.isEmpty() ? 1 : list.size()), total);
    }
}
