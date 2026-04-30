package com.livehealth.shared.base;

import com.livehealth.shared.base.pagination.Page;
import com.livehealth.shared.base.pagination.PageImpl;
import com.livehealth.shared.base.pagination.PageRequest;
import com.livehealth.shared.base.pagination.Pageable;
import com.livehealth.shared.base.pagination.Sort;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, ID> {

    @Inject
    protected EntityManager em;

    protected final Class<T> entityClass;

    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Optional<T> findById(ID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(entityClass, id));
    }

    public List<T> findAllById(Iterable<ID> ids) {
        if (ids == null) return List.of();
        List<T> result = new java.util.ArrayList<>();
        for (ID id : ids) {
            if (id != null) {
                T entity = em.find(entityClass, id);
                if (entity != null) result.add(entity);
            }
        }
        return result;
    }

    @jakarta.transaction.Transactional
    public T save(T entity) {
        if (entity == null) return null;
        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        if (id == null) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }

    @jakarta.transaction.Transactional
    public List<T> saveAll(Iterable<T> entities) {
        if (entities == null) return List.of();
        List<T> result = new java.util.ArrayList<>();
        for (T entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @jakarta.transaction.Transactional
    public void delete(T entity) {
        if (entity != null) {
            em.remove(em.contains(entity) ? entity : em.merge(entity));
        }
    }

    @jakarta.transaction.Transactional
    public void deleteById(ID id) {
        if (id != null) {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
        }
    }

    @jakarta.transaction.Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        if (entities != null) {
            for (T entity : entities) {
                delete(entity);
            }
        }
    }

    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).getResultList();
    }

    public Page<T> findAll(Pageable pageable) {
        if (pageable == null) {
            List<T> list = findAll();
            return new PageImpl<>(list, PageRequest.of(0, list.isEmpty() ? 1 : list.size()), list.size());
        }

        long total = count();

        StringBuilder jpql = new StringBuilder("SELECT e FROM ").append(entityClass.getSimpleName()).append(" e");
        if (pageable.getSort() != null && pageable.getSort().isSorted()) {
            jpql.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : pageable.getSort().getOrders()) {
                if (!first) jpql.append(", ");
                jpql.append("e.").append(order.getProperty()).append(" ").append(order.getDirection().name());
                first = false;
            }
        }

        var query = em.createQuery(jpql.toString(), entityClass);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<T> list = query.getResultList();
        return new PageImpl<>(list, pageable, total);
    }

    public boolean existsById(ID id) {
        if (id == null) return false;
        return em.find(entityClass, id) != null;
    }

    public long count() {
        return em.createQuery("SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class).getSingleResult();
    }

    public void flush() {
        em.flush();
    }
}
