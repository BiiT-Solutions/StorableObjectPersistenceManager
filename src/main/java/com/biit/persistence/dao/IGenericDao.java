package com.biit.persistence.dao;

/*-
 * #%L
 * Form Based Generic Persistence Manager
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
