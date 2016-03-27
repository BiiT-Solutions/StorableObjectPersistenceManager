package com.biit.persistence.utils;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContainerList<T> extends AbstractList<T> implements Serializable, IIndexedList, IDataContainer {
	private static final long serialVersionUID = 7107564701510121074L;

	private final List<T> view;
	private final Set<T> addedElements;
	private final Set<T> modifiedElements;
	private final Set<T> removedElements;
	private final Map<Object, T> codex;
	private final IDataProvider<T> provider;
	private final IKeyGenerator<T> keyGenerator;

	public ContainerList(IDataProvider<T> provider, IKeyGenerator<T> keyGenerator) {
		super();
		view = new ArrayList<>();
		addedElements = new LinkedHashSet<>();
		modifiedElements = new LinkedHashSet<>();
		removedElements = new LinkedHashSet<>();
		codex = new HashMap<>();
		this.provider = provider;
		this.keyGenerator = keyGenerator;
		view.addAll(provider.get(0, provider.size()));
		for (T element : view) {
			codex.put(keyGenerator.generate(element), element);
		}
	}

	public void update(T originalElement) {
		if (!addedElements.contains(originalElement)) {
			modifiedElements.add(originalElement);
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

	@Override
	public T remove(int index) {
		T elementToRemove = super.remove(index);
		removeElement(elementToRemove);
		return elementToRemove;
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

	public boolean containsKey(Object itemId) {
		return codex.containsKey(itemId);
	}

	public Collection<T> getAddedElements() {
		return Collections.unmodifiableCollection(addedElements);
	}

	public Collection<T> getModifiedElements() {
		return Collections.unmodifiableCollection(modifiedElements);
	}

	public Collection<T> getRemovedElements() {
		return Collections.unmodifiableCollection(removedElements);
	}
}
