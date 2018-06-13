package com.biit.persistence.dao;

import java.io.Serializable;

import javax.persistence.NoResultException;

import com.biit.persistence.entity.BaseStorableObject;

public interface IJpaBaseStorableObjectDao<EntityClass extends BaseStorableObject, PrimaryKeyClass extends Serializable> extends
		IJpaGenericDao<EntityClass, PrimaryKeyClass> {

	EntityClass getByComparatorId(String comparationId) throws NoResultException;
}
