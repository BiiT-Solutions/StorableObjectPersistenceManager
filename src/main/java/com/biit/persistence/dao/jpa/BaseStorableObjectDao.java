package com.biit.persistence.dao.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.biit.persistence.dao.IJpaBaseStorableObjectDao;
import com.biit.persistence.entity.BaseStorableObject;

public abstract class BaseStorableObjectDao<T extends BaseStorableObject, PrimaryKeyClass extends Serializable> extends GenericDao<T, PrimaryKeyClass>
		implements IJpaBaseStorableObjectDao<T, PrimaryKeyClass> {

	public BaseStorableObjectDao(Class<T> type) {
		super(type);
	}

	@Override
	public T getByComparatorId(String comparationId) {
		// Get the criteria builder instance from entity manager
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getEntityClass());
		// Tell to criteria query which tables/entities you want to fetch
		Root<T> typesRoot = criteriaQuery.from(getEntityClass());

		List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(criteriaBuilder.equal(typesRoot.get("comparationId"), comparationId));
		criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));

		return getEntityManager().createQuery(criteriaQuery).getSingleResult();
	}
}
