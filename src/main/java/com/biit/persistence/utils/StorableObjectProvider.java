package com.biit.persistence.utils;

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

}
