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

import com.biit.persistence.entity.BaseStorableObject;

public class StorableObjectContainer<T extends BaseStorableObject> extends ContainerList<T> {
    private static final long serialVersionUID = 1845024750896349696L;

    public StorableObjectContainer(Class<T> clazz, StorableObjectProvider<T> provider) {
        super(clazz, provider, new IKeyGenerator<T>() {

            @Override
            public Object generate(T object) {
                return object.getComparationId();
            }
        });
    }

}
