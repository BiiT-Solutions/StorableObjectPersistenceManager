package com.biit.persistence.dao.hibernate;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.biit.persistence.dao.IStorableObjectEntityDao;
import com.biit.persistence.entity.StorableObject;

/**
 * Only for using to get row count. Not possible to retrieve all Storables Objects from a database as it is now.
 */
@Repository
public class StorableObjectEntityDao extends GenericDao<StorableObject> implements IStorableObjectEntityDao {

	public StorableObjectEntityDao() {
		super(StorableObject.class);
	}

	@Override
	protected void initializeSets(List<StorableObject> elements) {
		// Not valid!
	}

}
