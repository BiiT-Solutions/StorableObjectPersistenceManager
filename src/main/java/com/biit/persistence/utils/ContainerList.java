package com.biit.persistence.utils;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContainerList<T> extends AbstractList<T> implements Serializable, IIndexedList, IDataContainer {
	private static final long serialVersionUID = 7107564701510121074L;

	private final List<T> view;
	private final List<T> addedElements;
	private final List<T> modifiedElements;
	private final List<T> removedElements;
	private final Map<Object, T> codex;
	private final IDataProvider<T> provider;
	private final IKeyGenerator<T> keyGenerator;

	public ContainerList(IDataProvider<T> provider, IKeyGenerator<T> keyGenerator) {
		super();
		view = new ArrayList<>();
		addedElements = new ArrayList<>();
		modifiedElements = new ArrayList<>();
		removedElements = new ArrayList<>();
		codex = new HashMap<>();
		this.provider = provider;
		this.keyGenerator = keyGenerator;
		view.addAll(provider.get(0, provider.size()));
		for(T element: view){
			codex.put(keyGenerator.generate(element), element);
		}
	}

	public void update(T originalElement, T modifiedElement) {
		set(view.indexOf(originalElement), modifiedElement);
		if (addedElements.contains(originalElement)) {
			addedElements.remove(originalElement);
			addedElements.add(modifiedElement);
		} else {
			if (modifiedElements.contains(originalElement)) {
				modifiedElements.remove(originalElement);
			}
			modifiedElements.add(modifiedElement);
		}
	}

	@Override
	public T set(int index, T element) {
		T currentElement = view.set(index, element);
		if (currentElement != null) {
			removeElement(currentElement);
			addElement(currentElement);
		}
		return currentElement;
	}

	@Override
	public boolean add(T element) {
		if (!view.add(element)) {
			return false;
		}
		addElement(element);
		return true;
	}

	private void addElement(T element) {
		addedElements.add(element);
		codex.put(keyGenerator.generate(element), element);
	}

	@Override
	public void add(int index, T element) {
		view.add(index, element);
		addElement(element);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		T elementToRemove = (T) o;
		if (!view.remove(elementToRemove)) {
			return false;
		}
		removeElement(elementToRemove);
		return true;
	}

	private void removeElement(T elementToRemove) {
		codex.remove(keyGenerator.generate(elementToRemove));
		if (addedElements.contains(elementToRemove)) {
			addedElements.remove(elementToRemove);
		} else {
			if (modifiedElements.contains(elementToRemove)) {
				modifiedElements.remove(elementToRemove);
			}
			removedElements.add(elementToRemove);
		}
	}

	@Override
	public T get(int index) {
		return view.get(index);
	}

	@Override
	public int size() {
		return view.size();
	}

	@Override
	public T getByKey(Object key) {
		return codex.get(key);
	}

	@Override
	public void commit() {
		for (T element : removedElements) {
			provider.remove(element);
		}
		for (T element : modifiedElements) {
			provider.update(element);
		}
		for (T element : addedElements) {
			provider.add(element);
		}
	}

	public IDataProvider<T> getProvider() {
		return provider;
	}

	public IKeyGenerator<T> getKeyGenerator() {
		return keyGenerator;
	}

	public Collection<Object> keys() {
		return codex.keySet();
	}
	
	public Object getIdByIndex(int index) {
		return getKeyGenerator().generate(get(index));
	}
}
