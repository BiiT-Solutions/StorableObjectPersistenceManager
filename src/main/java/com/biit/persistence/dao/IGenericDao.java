package com.biit.persistence.dao;

import com.biit.persistence.dao.exceptions.ElementCannotBePersistedException;
import com.biit.persistence.dao.exceptions.UnexpectedEntityDatabaseException;
import com.biit.persistence.entity.StorableObject;
import com.biit.persistence.entity.exceptions.ElementCannotBeRemovedException;

import java.util.List;

public interface IGenericDao<T extends StorableObject> extends IStorableObjectDao {

    /**
     * Get all elements stored into the database.
     *
     * @return
     * @throws UnexpectedEntityDatabaseException
     */
    List<T> getAll() throws UnexpectedEntityDatabaseException;

    /**
     * Saves or update an element.
     *
     * @param entity
     * @return
     * @throws UnexpectedEntityDatabaseException
     * @throws ElementCannotBePersistedException
     */
    T makePersistent(T entity) throws UnexpectedEntityDatabaseException, ElementCannotBePersistedException;

    /**
     * Gets the total number of elements.
     *
     * @return
     * @throws UnexpectedEntityDatabaseException
     */
    int getRowCount() throws UnexpectedEntityDatabaseException;

    /**
     * Gets one element by id.
     *
     * @param id
     * @return
     * @throws UnexpectedEntityDatabaseException
     */
    T read(Long id) throws UnexpectedEntityDatabaseException;

    /**
     * Remove all elements from database.
     *
     * @throws UnexpectedEntityDatabaseException
     */
    void removeAll() throws UnexpectedEntityDatabaseException;

    /**
     * Persists a list of objects.
     *
     * @param entities
     * @return
     * @throws UnexpectedEntityDatabaseException
     */
    List<T> makePersistent(List<T> entities) throws UnexpectedEntityDatabaseException;

    /**
     * Clear the entire 2nd level cache.
     */
    void evictAllCache();

    void makeTransient(T entity) throws UnexpectedEntityDatabaseException, ElementCannotBeRemovedException;
}
