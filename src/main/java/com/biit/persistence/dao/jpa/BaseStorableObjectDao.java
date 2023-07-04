package com.biit.persistence.dao.jpa;

import com.biit.persistence.dao.IJpaBaseStorableObjectDao;
import com.biit.persistence.entity.BaseStorableObject;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseStorableObjectDao<EntityClass extends BaseStorableObject, PrimaryKeyClass extends Serializable> extends
        GenericDao<EntityClass, PrimaryKeyClass> implements IJpaBaseStorableObjectDao<EntityClass, PrimaryKeyClass> {

    public BaseStorableObjectDao(Class<EntityClass> type) {
        super(type);
    }

    @Override
    public EntityClass merge(EntityClass entity) {
        entity.setUpdateTime();
        return super.merge(entity);
    }

    @Override
    public EntityClass getByComparatorId(String comparationId) throws NoResultException {
        // Get the criteria builder instance from entity manager
        final CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        final CriteriaQuery<EntityClass> criteriaQuery = criteriaBuilder.createQuery(getEntityClass());
        // Tell to criteria query which tables/entities you want to fetch
        final Root<EntityClass> typesRoot = criteriaQuery.from(getEntityClass());

        final List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(typesRoot.get("comparationId"), comparationId));
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[]{})));

        return getEntityManager().createQuery(criteriaQuery).getSingleResult();
    }
}
