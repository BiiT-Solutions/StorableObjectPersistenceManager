package com.biit.persistence.dao;

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
