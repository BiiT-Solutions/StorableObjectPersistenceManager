package com.biit.persistence.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class EditableLazyList<T> extends LazyList<T> {
	private static final long serialVersionUID = -6355280347802892380L;

	private final List<T> addedElements;
	private final List<T> updatedElements;
	private final List<T> removedElements;
	private final HashMap<T, Integer> elementPositions;

	public EditableLazyList(OrdedEntityProvider<T> entityProvider, int pageSize, int maxPages) {
		this(entityProvider, entityProvider, entityProvider, pageSize, maxPages);
	}

	public EditableLazyList(OrdedEntityProvider<T> orderedPageProvider, PagingProvider<T> pageProvider,
			com.biit.persistence.utils.LazyList.CountProvider countProvider, int pageSize, int maxPages) {
		super(orderedPageProvider, pageProvider, countProvider, pageSize, maxPages);
		addedElements = new ArrayList<>();
		updatedElements = new ArrayList<>();
		removedElements = new ArrayList<>();
		elementPositions = new HashMap<>();
	}

	@Override
	public boolean add(T item) {
		boolean result = addedElements.add(item);
		if (result == false) {
			return false;
		}
		elementPositions.clear();
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object item) {
		if (addedElements.contains(item)) {
			addedElements.remove(item);
		} else {
			if (updatedElements.contains(item)) {
				updatedElements.remove(item);
			}
			removedElements.add((T) item);
		}
		elementPositions.clear();
		return true;
	}

	public boolean update(T item) {
		if (addedElements.contains(item)) {
			addedElements.remove(item);
			addedElements.add(item);
			return true;
		} else if (updatedElements.contains(item)) {
			updatedElements.remove(item);
			updatedElements.add(item);
			return true;
		} else if (removedElements.contains(item)) {
			return false;
		} else if (super.contains(item)) {
			removedElements.add(item);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean contains(Object o) {
		if (addedElements.contains(o) || updatedElements.contains(o)) {
			return true;
		} else if (removedElements.contains(o)) {
			return false;
		} else {
			return super.contains(o);
		}
	}

	@Override
	public void clearCache() {
		elementPositions.clear();
		super.clearCache();
	}

	@Override
	public int size() {
		return super.size() + addedElements.size() - removedElements.size();
	}

	@Override
	public int indexOf(Object o) {
		Iterator<T> itr = iterator();
		int index = 0;
		while (itr.hasNext()) {
			if (Objects.equals(itr.next(), o)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	@Override
	public T get(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}

		int i = 0, iDb = 0, iAdded = 0;
		// Calcule current order
		for (; i < index && iAdded < addedElements.size() && iDb < super.size(); i++) {
			T dbElement = getMostRecentVersion(iDb);
			T addedElement = addedElements.get(iAdded);
			if (pageOrderedProvider.compare(dbElement, addedElement) < 0) {
				iDb++;
			} else {
				iAdded++;
			}
		}

		// If index has been reached and db and addCache still has elements
		if (i == index && iAdded < addedElements.size() && iDb < super.size()) {
			T dbElement = getMostRecentVersion(iDb);
			T addedElement = addedElements.get(iAdded);
			if (pageOrderedProvider.compare(dbElement, addedElement) < 0) {
				return dbElement;
			} else {
				return addedElement;
			}
		} else {
			if (i <= index && iDb < super.size()) {
				return super.get(iDb + index - i);
			} else {
				return addedElements.get(iAdded + index - i);
			}
		}
	}

	private T getMostRecentVersion(int dbIndex) {
		T element = super.get(dbIndex);
		int updatedIndex = updatedElements.indexOf(element);
		if (updatedIndex == -1) {
			return element;
		} else {
			return updatedElements.get(updatedIndex);
		}
	}
}
