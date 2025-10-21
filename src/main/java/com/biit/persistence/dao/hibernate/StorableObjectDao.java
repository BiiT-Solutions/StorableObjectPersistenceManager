package com.biit.persistence.dao.hibernate;

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

import com.biit.persistence.dao.IStorableObjectDao;
import com.biit.persistence.dao.exceptions.UnexpectedEntityDatabaseException;
import com.biit.persistence.entity.BaseStorableObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.sql.Timestamp;
import java.util.Date;

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
        final BaseStorableObject storableObject = (BaseStorableObject) entity;
        if (storableObject.getCreationTime() == null) {
            storableObject.setCreationTime(new Timestamp(new Date().getTime()));
        }
    }

    protected void setUpdateInfo(T entity) {
        final BaseStorableObject treeObject = entity;
        treeObject.setUpdateTime(new Timestamp(new Date().getTime()));
    }

    @Override
    public void deleteStorableObject(BaseStorableObject entity) throws UnexpectedEntityDatabaseException {
        if (entity.getId() != null) {
            final Session session = getSessionFactory().getCurrentSession();
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
