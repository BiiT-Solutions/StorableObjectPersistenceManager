package com.biit.persistence.utils;

import java.util.Collections;
import java.util.List;

public class LazyListPage<T> implements Comparable<LazyListPage<?>> {

	private final int pageNumber;
	private final List<T> content;

	public LazyListPage(int pageNumber, List<T> content) {
		this.pageNumber = pageNumber;
		this.content = Collections.unmodifiableList(content);
	}

	public int getPageNumber() {
		return pageNumber;
	}

	@Override
	public int compareTo(LazyListPage<?> o) {
		return Integer.compare(pageNumber, o.getPageNumber());
	}

	public T get(int indexOnPage) {
		return content.get(indexOnPage);
	}

	public List<T> getContent() {
		return content;
	}
}
