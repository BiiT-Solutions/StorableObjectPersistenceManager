package com.biit.persistence.dao;

import com.biit.persistence.dao.exceptions.UnexpectedEntityDatabaseException;
import com.biit.persistence.entity.BaseStorableObject;
import org.hibernate.SessionFactory;

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
     * @param entity
     * @throws UnexpectedEntityDatabaseException
     */
    void deleteStorableObject(BaseStorableObject entity) throws UnexpectedEntityDatabaseException;

}
