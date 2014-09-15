package com.biit.persistence.dao;

import java.util.List;

import com.biit.persistence.entity.StorableObject;

public interface IGenericDao<T extends StorableObject> {

	/**
	 * Get all elements stored into the database.
	 * 
	 * @return
	 */
	List<T> getAll();

	/**
	 * Saves or update an element.
	 * 
	 * @param planningEvent
	 * @return
	 */
	T makePersistent(T entity);

	/**
	 * Delete the persistence of the element (but not the java object).
	 * 
	 * 
	 * @param planningEvent
	 */
	void makeTransient(T entity);

	/**
	 * Gets the total number of elements.
	 * 
	 * @return
	 */
	int getRowCount();

	/**
	 * Gets one element by id.
	 * 
	 * @param id
	 * @return
	 */
	T read(Long id);

	/**
	 * Remove all elements from database.
	 * 
	 * @return
	 */
	void removeAll();

	/**
	 * Persists a list of objects.
	 * 
	 * @param entities
	 * @return
	 */
	List<T> makePersistent(List<T> entities);
}
