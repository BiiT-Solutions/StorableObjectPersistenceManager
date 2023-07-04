package com.biit.persistence.dao;

import com.biit.persistence.entity.BaseStorableObject;
import jakarta.persistence.NoResultException;

import java.io.Serializable;

public interface IJpaBaseStorableObjectDao<EntityClass extends BaseStorableObject, PrimaryKeyClass extends Serializable> extends
        IJpaGenericDao<EntityClass, PrimaryKeyClass> {

    EntityClass getByComparatorId(String comparationId) throws NoResultException;
}
