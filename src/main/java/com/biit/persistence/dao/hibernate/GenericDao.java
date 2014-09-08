package com.biit.persistence.dao.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.biit.persistence.dao.IGenericDao;

public abstract class GenericDao<T> extends StorableObjectDao<T> implements IGenericDao<T> {
	// Recomended values are [15-25]. Bigger values reduce database access but increase CPU consumption.
	private final static int MAX_OBJETS_PER_SESSION = 25;

	private Class<T> type;

	@Autowired
	private EntityManager entityManager = null;

	public GenericDao(Class<T> type) {
		super();
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

	protected Session getSession() {
		Session session = entityManager.unwrap(Session.class);
		return session;
	}

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Transactional
	public T makePersistent(T entity) {
		setCreationInfo(entity);
		setUpdateInfo(entity);
		entityManager.persist(entity);
		return entity;
	}

	@Override
	public void makeTransient(T entity) {
		entityManager.remove(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public T read(Long id) {
		if (id == null) {
			return null;
		} else {
			return entityManager.find(type, id);
		}
	}

	@Override
	public Long getRowCount() {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = qb.createQuery(Long.class);
		cq.select(qb.count(cq.from(getType())));
		cq.where(/* your stuff */);
		return entityManager.createQuery(cq).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll() {
		return entityManager.createQuery("select o from " + type.getName() + " o").getResultList();
	}

	/**
	 * Truncates the table.
	 */
	@Override
	public void removeAll() {
		List<T> elements = getAll();
		for (T element : elements) {
			makeTransient(element);
		}
	}
}
