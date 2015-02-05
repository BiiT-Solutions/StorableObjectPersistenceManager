package com.biit.persistence.dao;

import java.util.List;

import com.biit.persistence.dao.exceptions.ElementCannotBePersistedException;
import com.biit.persistence.dao.exceptions.UnexpectedDatabaseException;
import com.biit.persistence.entity.StorableObject;
import com.biit.persistence.entity.exceptions.ElementCannotBeRemovedException;

public interface IGenericDao<T extends StorableObject> extends IStorableObjectDao {

	/**
	 * Get all elements stored into the database.
	 * 
	 * @return
	 * @throws UnexpectedDatabaseException
	 */
	List<T> getAll() throws UnexpectedDatabaseException;

	/**
	 * Saves or update an element.
	 * 
	 * @param planningEvent
	 * @return
	 * @throws UnexpectedDatabaseException
	 * @throws ElementCannotBePersistedException
	 */
	T makePersistent(T entity) throws UnexpectedDatabaseException, ElementCannotBePersistedException;

	/**
	 * Gets the total number of elements.
	 * 
	 * @return
	 * @throws UnexpectedDatabaseException
	 */
	int getRowCount() throws UnexpectedDatabaseException;

	/**
	 * Gets one element by id.
	 * 
	 * @param id
	 * @return
	 * @throws UnexpectedDatabaseException
	 */
	T read(Long id) throws UnexpectedDatabaseException;

	/**
	 * Remove all elements from database.
	 * 
	 * @return
	 * @throws UnexpectedDatabaseException
	 */
	void removeAll() throws UnexpectedDatabaseException;

	/**
	 * Persists a list of objects.
	 * 
	 * @param entities
	 * @return
	 * @throws UnexpectedDatabaseException
	 */
	List<T> makePersistent(List<T> entities) throws UnexpectedDatabaseException;

	/**
	 * Clear the entire 2nd level cache.
	 */
	void evictAllCache();

	void makeTransient(T entity) throws UnexpectedDatabaseException, ElementCannotBeRemovedException;
}
