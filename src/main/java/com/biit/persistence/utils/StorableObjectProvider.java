package com.biit.persistence.utils;

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

import com.biit.persistence.dao.IJpaGenericDao;
import com.biit.persistence.entity.BaseStorableObject;
import com.biit.persistence.entity.exceptions.ElementCannotBeRemovedException;
import com.biit.persistence.logger.StorableObjectLogger;

public abstract class StorableObjectProvider<T extends BaseStorableObject> implements IDataProvider<T> {

    private final IJpaGenericDao<T, ?> dao;

    public StorableObjectProvider(IJpaGenericDao<T, ?> dao) {
        this.dao = dao;
    }

    @Override
    public void add(T element) {
        dao.makePersistent(element);
    }

    @Override
    public void update(T element) {
        dao.merge(element);
    }

    @Override
    public void remove(T element) {
        try {
            dao.makeTransient(element);
        } catch (ElementCannotBeRemovedException e) {
            StorableObjectLogger.errorMessage(this.getClass().getName(), e);
        }
    }

    public IJpaGenericDao<T, ?> getDao() {
        return dao;
    }

    public T newEntity() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
