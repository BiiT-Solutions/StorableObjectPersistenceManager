package com.biit.persistence.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import com.biit.persistence.entity.exceptions.ElementCannotBeRemovedException;

public interface IJpaGenericDao<EntityClass, PrimaryKeyClass extends Serializable> {

	void makeTransient(EntityClass entity) throws ElementCannotBeRemovedException;

	void makePersistent(EntityClass entity);

	EntityClass merge(EntityClass entity);

	EntityClass get(PrimaryKeyClass id);

	int getRowCount();

	List<EntityClass> getAll();

	void evictAllCache();

	EntityManager getEntityManager();
}
