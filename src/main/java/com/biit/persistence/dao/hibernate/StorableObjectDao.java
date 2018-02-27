package com.biit.persistence.dao.hibernate;

import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.biit.persistence.dao.IStorableObjectDao;
import com.biit.persistence.dao.exceptions.UnexpectedEntityDatabaseException;
import com.biit.persistence.entity.BaseStorableObject;

public class StorableObjectDao<T extends BaseStorableObject> implements IStorableObjectDao {

	private SessionFactory sessionFactory = null;

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected void setCreationInfo(T entity) {
		BaseStorableObject storableObject = (BaseStorableObject) entity;
		if (storableObject.getCreationTime() == null) {
			storableObject.setCreationTime(new Timestamp(new Date().getTime()));
		}
	}

	protected void setUpdateInfo(T entity) {
		BaseStorableObject treeObject = (BaseStorableObject) entity;
		treeObject.setUpdateTime(new Timestamp(new Date().getTime()));
	}

	@Override
	public void deleteStorableObject(BaseStorableObject entity) throws UnexpectedEntityDatabaseException {
		if (entity.getId() != null) {
			Session session = getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try {
				session.delete(entity);
				session.flush();
				session.getTransaction().commit();
			} catch (RuntimeException e) {
				session.getTransaction().rollback();
				throw new UnexpectedEntityDatabaseException(e.getMessage(), e);
			}
		}
	}

}
