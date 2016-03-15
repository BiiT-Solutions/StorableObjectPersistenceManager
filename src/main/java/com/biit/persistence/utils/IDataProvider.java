package com.biit.persistence.utils;

import java.util.Collection;

public interface IDataProvider<T> {

	void add(T element);

	void update(T element);

	void remove(T element);

	int size();

	Collection<? extends T> get(int i, int size);
}
