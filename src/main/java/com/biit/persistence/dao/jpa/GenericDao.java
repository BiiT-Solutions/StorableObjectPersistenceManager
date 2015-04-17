package com.biit.persistence.dao.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.biit.persistence.dao.IJpaGenericDao;

public abstract class GenericDao<EntityClass, PrimaryKeyClass extends Serializable> implements
		IJpaGenericDao<EntityClass, PrimaryKeyClass> {

	protected Class<EntityClass> entityClass;

	@Override
	public abstract EntityManager getEntityManager();

	public GenericDao(Class<EntityClass> entityClass) {
		this.entityClass = entityClass;
	}

	@Override
	public void makePersistent(EntityClass entity) {
		getEntityManager().persist(entity);
	}

	@Override
	public void makeTransient(EntityClass entity) {
		getEntityManager().remove(getEntityManager().contains(entity) ? entity : getEntityManager().merge(entity));
	}

	@Override
	public EntityClass get(PrimaryKeyClass id) {
		return getEntityManager().find(entityClass, id);
	}

	@Override
	public int getRowCount() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<EntityClass> root = query.from(entityClass);

		query.select(cb.count(root));
		return getEntityManager().createQuery(query).getSingleResult().intValue();
	}

	@Override
	public List<EntityClass> getAll() {
		CriteriaQuery<EntityClass> query = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
		query.select(query.from(entityClass));
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

	public Class<EntityClass> getEntityClass() {
		return entityClass;
	}
}
