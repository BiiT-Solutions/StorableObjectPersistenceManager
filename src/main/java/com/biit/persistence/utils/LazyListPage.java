package com.biit.persistence.utils;

/*-
 * #%L
 * Form Based Generic Persistence Manager
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class LazyListPage<T> implements Comparable<LazyListPage<?>>, Serializable {

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
