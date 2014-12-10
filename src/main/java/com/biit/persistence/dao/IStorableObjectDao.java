package com.biit.persistence.dao;

import org.hibernate.SessionFactory;

import com.biit.persistence.dao.exceptions.UnexpectedDatabaseException;
import com.biit.persistence.entity.StorableObject;

public interface IStorableObjectDao {

	/**
	 * Gets current sessionFactory.
	 * 
	 * @return
	 */
	SessionFactory getSessionFactory();

	void setSessionFactory(SessionFactory sessionFactory);

	/**
	 * Delete the persistence of the element (but not the java object).
	 * 
	 * 
	 * @param planningEvent
	 * @throws UnexpectedDatabaseException
	 */
	void deleteStorableObject(StorableObject entity) throws UnexpectedDatabaseException;

}
