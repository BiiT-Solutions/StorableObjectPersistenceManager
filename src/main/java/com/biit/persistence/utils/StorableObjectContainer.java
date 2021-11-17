package com.biit.persistence.utils;

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
