package com.livehealth.user;

import com.livehealth.shared.base.BaseRepository;
import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageImpl;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.pagination.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository extends BaseRepository<User, UUID> {

    public UserRepository() {
        super(User.class);
    }

    public Optional<User> findByEmail(String email) {
        return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList().stream()
                .findFirst();
    }

    public boolean existsUserByEmail(String email) {
        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    public boolean existsUserByPhone(String phone) {
        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.phone = :phone", Long.class)
                .setParameter("phone", phone)
                .getSingleResult();
        return count > 0;
    }

    public long countByAddressId(UUID addressId) {
        return em.createQuery("SELECT COUNT(u) FROM User u WHERE u.address.id = :addressId", Long.class)
                .setParameter("addressId", addressId)
                .getSingleResult();
    }

    public Page<User> findUserByEmailContainingIgnoreCase(String email, Pageable pageable) {
        String countJpql = "SELECT COUNT(u) FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))";
        long total = em.createQuery(countJpql, Long.class)
                .setParameter("email", email)
                .getSingleResult();

        StringBuilder jpql = new StringBuilder("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))");
        if (pageable != null && pageable.getSort() != null && pageable.getSort().isSorted()) {
            jpql.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : pageable.getSort().getOrders()) {
                if (!first) jpql.append(", ");
                jpql.append("u.").append(order.getProperty()).append(" ").append(order.getDirection().name());
                first = false;
            }
        }

        var query = em.createQuery(jpql.toString(), User.class)
                .setParameter("email", email);

        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<User> list = query.getResultList();
        return new PageImpl<>(list, pageable != null ? pageable : PageRequest.of(0, list.isEmpty() ? 1 : list.size()), total);
    }

    public Page<User> findUserByPhoneContaining(String phone, Pageable pageable) {
        String countJpql = "SELECT COUNT(u) FROM User u WHERE u.phone LIKE CONCAT('%', :phone, '%')";
        long total = em.createQuery(countJpql, Long.class)
                .setParameter("phone", phone)
                .getSingleResult();

        StringBuilder jpql = new StringBuilder("SELECT u FROM User u WHERE u.phone LIKE CONCAT('%', :phone, '%')");
        if (pageable != null && pageable.getSort() != null && pageable.getSort().isSorted()) {
            jpql.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : pageable.getSort().getOrders()) {
                if (!first) jpql.append(", ");
                jpql.append("u.").append(order.getProperty()).append(" ").append(order.getDirection().name());
                first = false;
            }
        }

        var query = em.createQuery(jpql.toString(), User.class)
                .setParameter("phone", phone);

        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<User> list = query.getResultList();
        return new PageImpl<>(list, pageable != null ? pageable : PageRequest.of(0, list.isEmpty() ? 1 : list.size()), total);
    }

    public List<Object[]> countUserByDay(LocalDate startDate) {
        return em.createQuery("SELECT u.createdAt as date, COUNT(u) as count FROM User u WHERE u.createdAt >= :startDate GROUP BY u.createdAt", Object[].class)
                .setParameter("startDate", startDate)
                .getResultList();
    }

    public List<Object[]> countUserByMonth(LocalDate startDate) {
        return em.createQuery(
                "SELECT FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m'), COUNT(u) " +
                "FROM User u " +
                "WHERE u.createdAt >= :startDate " +
                "GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m') " +
                "ORDER BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m') ASC", Object[].class)
                .setParameter("startDate", startDate)
                .getResultList();
    }
}
