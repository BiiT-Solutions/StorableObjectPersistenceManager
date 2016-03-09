package com.biit.persistence.utils;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class LazyList<T> extends AbstractList<T> implements Serializable {
	private static final long serialVersionUID = 7107564701510121074L;

	// Split into subinterfaces for better Java 8 lambda support
	/**
	 * Interface via the LazyList retrieves entities
	 *
	 * @param <T>
	 *            The type of the objects in the list
	 */
	public interface PagingProvider<T> extends Serializable {

		/**
		 * Fetches entities from the backend.
		 *
		 * @param firstRow
		 *            the index of first row that should be fetched
		 * @param total
		 *            is the number of rows that should be fetched.
		 * @return a sub list from given first index
		 */
		public List<T> getEntities(int firstRow, int total);
	}

	/**
	 * Interface via the LazyList retrieves entities with an order
	 *
	 * @param <T>
	 */
	public interface PagingOrderedProvider<T> extends Serializable {

		/**
		 * Fetches entities form the backend ordered with property/order.
		 *
		 * @param firstRow
		 *            the index of first row that should be fetched
		 * @param total
		 *            is the number of rows that should be fetched.
		 * @return a sub list from given first index
		 */
		public List<T> getEntities(int firstRow, int total, String[] propertyNames, Order[] order);

		/**
		 * This method works the same way as a normal compare to returns -1, 0
		 * or 1 if first is less than, equal or greater than
		 * 
		 * @param first
		 * @param second
		 * @return
		 */
		public int compare(T first, T second);
	}

	/**
	 * LazyList detects the size of the "simulated" list with via this
	 * interface. Backend call is cached as COUNT queries in databases are
	 * commonly heavy.
	 */
	public interface CountProvider extends Serializable {

		/**
		 * @return the count of entities listed in the LazyList
		 */
		public int size();
	}

	public interface EntityProvider<T> extends PagingProvider<T>, CountProvider {
	}

	public interface OrdedEntityProvider<T> extends EntityProvider<T>, PagingOrderedProvider<T> {
	}

	public interface LazyListChanged<T> {
		public void collectionChanged(LazyList<T> changedList);
	}

	// Configuration params
	protected final PagingOrderedProvider<T> pageOrderedProvider;
	private final PagingProvider<T> pageProvider;
	private final CountProvider countProvider;
	private final int pageSize;
	private final int maxPages;

	private List<LazyListPage<T>> cachedPages;
	private final HashSet<T> cachedElements;

	private int pageIndex = -1;
	private Integer cachedSize;

	private final List<LazyListChanged<T>> lazyListChangedListeners;
	private transient WeakHashMap<T, Integer> indexCache;
	private String[] propertyNames;
	private Order[] order;

	public LazyList(PagingOrderedProvider<T> pageOrderedProvider, PagingProvider<T> pageProvider,
			CountProvider countProvider, int pageSize, int maxPages) {
		this.pageOrderedProvider = pageOrderedProvider;
		this.pageProvider = pageProvider;
		this.countProvider = countProvider;
		this.pageSize = pageSize;
		this.maxPages = maxPages;

		cachedPages = new ArrayList<>();
		cachedElements = new HashSet<>();
		lazyListChangedListeners = new ArrayList<>();
	}

	public LazyList(EntityProvider<T> entityProvider, int pageSize, int maxPages) {
		this(null, entityProvider, entityProvider, pageSize, maxPages);
	}

	public LazyList(OrdedEntityProvider<T> entityProvider, int pageSize, int maxPages) {
		this(entityProvider, entityProvider, entityProvider, pageSize, maxPages);
	}

	@Override
	public T get(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}

		// Get pageIndex and index in the page
		final int pageIndexForReqest = index / pageSize;
		final int indexOnPage = index % pageSize;

		// Find page from cache
		LazyListPage<T> page = getPage(pageIndexForReqest);

		if (page == null) {
			page = loadPage(pageIndexForReqest);
		}
		final T get = page.get(indexOnPage);
		return get;
	}

	private LazyListPage<T> loadPage(int pageIndexForReqest) {
		// Create a new page
		LazyListPage<T> newPage = new LazyListPage<>(pageIndexForReqest, findEntities(pageIndexForReqest * pageSize));
		cachedPages.add(newPage);
		// Balance the cache if exceeded maxPage number
		if (cachedPages.size() >= maxPages) {
			int index = cachedPages.indexOf(newPage);
			balanceCachePages(index);
		}
		return null;
	}

	private void balanceCachePages(int index) {
		if (index < (maxPages / 2)) {
			// remove last page
			removeCachedElements(cachedPages.get(cachedPages.size() - 1));
			cachedPages.remove(cachedPages.size() - 1);
		} else {
			removeCachedElements(cachedPages.get(0));
			cachedPages.remove(0);
		}
	}

	private LazyListPage<T> getPage(int pageIndexForReqest) {
		// TODO optimize with binary search
		for (LazyListPage<T> page : cachedPages) {
			if (page.getPageNumber() == pageIndex) {
				return page;
			}
		}
		return null;
	}

	private List<T> findEntities(int firstRow) {
		List<T> page;
		if (pageOrderedProvider == null) {
			page = pageProvider.getEntities(firstRow, pageSize);
		} else {
			page = pageOrderedProvider.getEntities(firstRow, pageSize, propertyNames, order);
		}
		// Check that none of the elements in the page are already contained. If
		// a element recovered is in the current cache that means that the
		// collection in the database has changed
		for (T entity : page) {
			if (cachedElements.contains(entity)) {
				// Database query result has changed. Clear cache and call the
				// registered LazyListChanged listeners.
				clearCache();
				fireLazyListChangedListeners();
			}
		}

		// Now register the new entities
		for (T entity : page) {
			cachedElements.add(entity);
		}

		return page;
	}

	@Override
	public int size() {
		// If size is not cached, query database.
		if (cachedSize == null) {
			cachedSize = countProvider.size();
		}
		return cachedSize;
	}

	private void removeCachedElements(LazyListPage<T> page) {
		for (T element : page.getContent()) {
			cachedElements.remove(element);
			indexCache.remove(element);
		}
	}

	public void clearCache() {
		cachedPages.clear();
		cachedElements.clear();
		if (indexCache != null) {
			indexCache.clear();
		}
	}

	public void addLazyListChangedListener(LazyListChanged<T> listener) {
		lazyListChangedListeners.add(listener);
	}

	public void removeLazyListChangedListener(LazyListChanged<T> listener) {
		lazyListChangedListeners.remove(listener);
	}

	private void fireLazyListChangedListeners() {
		for (LazyListChanged<T> listener : lazyListChangedListeners) {
			listener.collectionChanged(this);
		}
	}

	private Map<T, Integer> getIndexCache() {
		if (indexCache == null) {
			indexCache = new WeakHashMap<T, Integer>(pageSize * 3);
		}
		return indexCache;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int indexOf(Object o) {
		// optimize: check the cached pages first
		Integer indexViaCache = getIndexCache().get(o);
		if (indexViaCache != null) {
			return indexViaCache;
		}
		for (LazyListPage<T> page : cachedPages) {
			int indexInPage = page.getContent().indexOf(o);
			if (indexInPage != -1) {
				indexViaCache = page.getPageNumber() * pageSize + indexInPage;
				getIndexCache().put((T) o, indexViaCache);
				return indexViaCache;
			}
		}
		return super.indexOf(o);
	}

	@Override
	public boolean contains(Object o) {
		// Although there would be the indexed version, vaadin sometimes calls
		// this. First check caches, then fall back to sluggish iterator :-(
		for (LazyListPage<T> page : cachedPages) {
			if (page.getContent().contains(o)) {
				return true;
			}
		}
		return super.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			private int index = -1;
			private final int size = size();

			@Override
			public boolean hasNext() {
				return index + 1 < size;
			}

			@Override
			public T next() {
				index++;
				return get(index);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Not supported.");
			}
		};
	}

	public void sort(String[] propertyNames, Order[] order) {
		this.propertyNames = propertyNames;
		this.order = order;
		if (pageOrderedProvider != null) {
			clearCache();
			fireLazyListChangedListeners();
		}
	}
}
