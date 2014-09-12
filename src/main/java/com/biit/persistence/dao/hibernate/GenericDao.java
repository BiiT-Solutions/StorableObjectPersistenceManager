package com.biit.persistence.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;

import com.biit.persistence.dao.IGenericDao;
import com.biit.persistence.entity.StorableObject;

public abstract class GenericDao<T extends StorableObject> extends StorableObjectDao<T> implements IGenericDao<T> {
	// Recommended values are [15-25]. Bigger values reduce database access but increase CPU consumption.
	private final static int MAX_OBJETS_PER_SESSION = 25;

	private Class<T> type;

	@Autowired
	private SessionFactory sessionFactory = null;

	public GenericDao(Class<T> type) {
		super();
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public T makePersistent(T entity) {
		setCreationInfo(entity);
		setUpdateInfo(entity);
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.saveOrUpdate(entity);
			session.flush();
			session.getTransaction().commit();
			return entity;
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public List<T> makePersistent(List<T> entities) {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		int objectsToStore = 0;
		try {
			for (int i = 0; i < entities.size(); i++) {
				setCreationInfo(entities.get(i));
				setUpdateInfo(entities.get(i));
				session.saveOrUpdate(entities.get(i));
				objectsToStore++;

				if (objectsToStore > MAX_OBJETS_PER_SESSION || i == entities.size() - 1) {
					session.flush();
					session.clear();
					objectsToStore = 0;
				}
			}
			session.getTransaction().commit();
			return entities;
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public void makeTransient(T entity) {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.delete(entity);
			session.flush();
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public T read(Long id) {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			@SuppressWarnings("unchecked")
			T object = (T) session.get(getType(), id);
			initializeSet(object);
			session.getTransaction().commit();
			return object;
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public int getRowCount() {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			Criteria criteria = session.createCriteria(getType());
			criteria.setProjection(Projections.rowCount());
			int rows = ((Long) criteria.uniqueResult()).intValue();
			session.getTransaction().commit();
			return rows;
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public List<T> getAll() {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			// session.createCriteria(getType()).list() is not working returns repeated elements due to
			// http://stackoverflow.com/questions/8758363/why-session-createcriteriaclasstype-list-return-more-object-than-in-list
			// if we have a list with eager fetch.
			Criteria criteria = session.createCriteria(getType());
			// This is executed in java side.
			criteria.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
			@SuppressWarnings("unchecked")
			List<T> objects = criteria.list();
			initializeSets(objects);
			session.getTransaction().commit();
			return objects;
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
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
	 * When using lazy loading, the sets must have a proxy to avoid a
	 * "LazyInitializationException: failed to lazily initialize a collection of..." error. This procedure must be
	 * called before closing the session.
	 * 
	 * @param planningEvent
	 */
	private void initializeSet(T element) {
		List<T> elements = new ArrayList<>();
		elements.add(element);
		initializeSets(elements);
	}

	/**
	 * When using lazy loading, the sets must have a proxy to avoid a
	 * "LazyInitializationException: failed to lazily initialize a collection of..." error. This procedure must be
	 * called before closing the session.
	 * 
	 * @param elements
	 */
	protected abstract void initializeSets(List<T> elements);
}
