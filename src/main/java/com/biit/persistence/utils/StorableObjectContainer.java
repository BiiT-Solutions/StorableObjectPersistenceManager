package com.biit.persistence.utils;

import com.biit.persistence.entity.StorableObject;

public class StorableObjectContainer<T extends StorableObject> extends ContainerList<T> {
	private static final long serialVersionUID = 1845024750896349696L;

	public StorableObjectContainer(StorableObjectProvider<T> provider) {
		super(provider, new IKeyGenerator<T>() {

			@Override
			public Object generate(T object) {
				return object.getComparationId();
			}
		});
	}
}
