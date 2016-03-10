package com.biit.persistence.utils;

import com.biit.persistence.dao.IJpaGenericDao;
import com.biit.persistence.entity.StorableObject;
import com.biit.persistence.entity.exceptions.ElementCannotBeRemovedException;
import com.biit.persistence.logger.StorableObjectLogger;

public abstract class StorableObjectProvider<T extends StorableObject> implements IDataProvider<T> {

	private IJpaGenericDao<T, ?> dao;

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

	@Override
	public int size() {
		return dao.getRowCount();
	}

}
