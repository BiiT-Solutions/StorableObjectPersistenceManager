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

import com.biit.persistence.entity.exceptions.ElementCannotBeRemovedException;
import jakarta.persistence.EntityManager;

import java.io.Serializable;
import java.util.List;

public interface IJpaGenericDao<EntityClass, PrimaryKeyClass extends Serializable> {

    void makeTransient(EntityClass entity) throws ElementCannotBeRemovedException;

    EntityClass makePersistent(EntityClass entity);

    EntityClass merge(EntityClass entity);

    EntityClass get(PrimaryKeyClass id);

    int getRowCount();

    List<EntityClass> getAll();

    void evictAllCache();

    EntityManager getEntityManager();

    void evictCache();

}
