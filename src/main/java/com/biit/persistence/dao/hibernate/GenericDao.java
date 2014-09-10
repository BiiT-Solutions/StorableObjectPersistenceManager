package com.biit.persistence.dao.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.TransactionRequiredException;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.biit.persistence.dao.IGenericDao;
import com.biit.persistence.entity.StorableObject;

public abstract class GenericDao<T extends StorableObject> extends StorableObjectDao<T> implements IGenericDao<T> {
	// Recommended values are [15-25]. Bigger values reduce database access but increase CPU consumption.
	private final static int MAX_OBJETS_PER_SESSION = 25;

	private Class<T> type;

	@PersistenceContext
	private EntityManager entityManager = null;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	public GenericDao(Class<T> type) {
		super();
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

	protected Session getSession() {
		Session session = getEntityManager().unwrap(Session.class);
		return session;
	}

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public T makePersistent(T entity) {
		setCreationInfo(entity);
		setUpdateInfo(entity);
		if (((StorableObject) entity).getId() == null) {
			getEntityManager().persist(entity);
			return entity;
		} else {
			T entityPersisted = getEntityManager().merge(entity);
			return entityPersisted;
		}
	}

	@Override
	@Transactional
	public void makeTransient(T entity) {
		getEntityManager().remove(entity);
	}

	@Override
	public T read(Long id) {
		if (id == null) {
			return null;
		} else {
			return getEntityManager().find(getType(), id);
		}
	}

	@Override
	public int getRowCount() {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(getType())));
		return entityManager.createQuery(criteriaQuery).getSingleResult().intValue();
	}

	@Override
	public List<T> getAll() {
		CriteriaQuery<T> criteria = getEntityManager().getCriteriaBuilder().createQuery(getType());
		criteria.select(criteria.from(getType()));
		List<T> ListOfEmailDomains = getEntityManager().createQuery(criteria).getResultList();
		return ListOfEmailDomains;

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

	/**
	 * Checks if a transaction has been correctly configured or not.
	 * 
	 * @throws TransactionRequiredException
	 */
	protected void checkTransactionNeeded() throws TransactionRequiredException {
		EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager
				.getResource(entityManagerFactory);
		if (emHolder == null) {
			throw new TransactionRequiredException("no transaction is in progress");
		}
	}
	
	protected boolean isManaged(T entity){
		return getEntityManager().contains(entity);
	}
}
