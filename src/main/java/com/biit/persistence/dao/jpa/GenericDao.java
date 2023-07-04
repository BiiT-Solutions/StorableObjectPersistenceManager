package com.biit.persistence.dao.jpa;

import com.biit.persistence.dao.IJpaGenericDao;
import com.biit.persistence.entity.exceptions.ElementCannotBeRemovedException;
import jakarta.persistence.Cache;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericDao<EntityClass, PrimaryKeyClass extends Serializable> implements IJpaGenericDao<EntityClass, PrimaryKeyClass> {

    private Class<EntityClass> entityClass;

    @Override
    public abstract EntityManager getEntityManager();

    public GenericDao(Class<EntityClass> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public EntityClass makePersistent(EntityClass entity) {
        if (entity == null) {
            throw new NullPointerException();
        }

        getEntityManager().persist(entity);
        // We force a flush due to in some cases a bidirectional relationships
        // needs a @ManyToOne(optional = false) to
        // perform an orphan removals. But without the flush, the optional
        // causes an exception due to the element is set
        // to null.
        // http://stackoverflow.com/questions/3068817/hibernate-triggering-constraint-violations-using-orphanremoval
        getEntityManager().flush();

        return entity;
    }

    @Override
    public EntityClass merge(EntityClass entity) {
        if (entity == null) {
            throw new NullPointerException();
        }
        final EntityClass managedEntity = getEntityManager().merge(entity);
        getEntityManager().flush();
        return managedEntity;
    }

    @Override
    public void makeTransient(EntityClass entity) throws ElementCannotBeRemovedException {
        if (entity != null) {
            getEntityManager().remove(getEntityManager().contains(entity) ? entity : getEntityManager().merge(entity));
        }
    }

    @Override
    public EntityClass get(PrimaryKeyClass id) {
        return getEntityManager().find(getEntityClass(), id);
    }

    @Override
    public int getRowCount() {
        final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);
        final Root<EntityClass> root = query.from(getEntityClass());

        query.select(cb.count(root));
        return getEntityManager().createQuery(query).getSingleResult().intValue();
    }

    @Override
    public List<EntityClass> getAll() {
        final CriteriaQuery<EntityClass> query = getEntityManager().getCriteriaBuilder().createQuery(getEntityClass());
        query.select(query.from(getEntityClass()));
        try {
            return getEntityManager().createQuery(query).getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<EntityClass>();
        }
    }

    @Override
    public void evictAllCache() {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    @Override
    public void evictCache() {
        final EntityManagerFactory factory = getEntityManager().getEntityManagerFactory();
        final Cache cache = factory.getCache();
        cache.evict(getEntityClass());
        getEntityManager().clear();
    }

    public Class<EntityClass> getEntityClass() {
        return entityClass;
    }
}
