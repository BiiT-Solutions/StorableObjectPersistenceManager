package com.biit.persistence.utils;

public interface IKeyGenerator<T> {

	Object generate(T object);

}
