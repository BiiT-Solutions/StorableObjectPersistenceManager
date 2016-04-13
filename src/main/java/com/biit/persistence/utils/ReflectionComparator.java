package com.biit.persistence.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;

import com.biit.persistence.logger.StorableObjectLogger;

public class ReflectionComparator<T> implements Comparator<T> {

	private final Object[] propertyId;
	private final boolean[] ascending;
	private final HashMap<String, PropertyDescriptor> propertyDescriptors;

	public ReflectionComparator(Class<T> clazz, Object[] propertyId, boolean[] ascending) throws IntrospectionException {
		super();
		this.propertyId = propertyId;
		this.ascending = ascending;
		this.propertyDescriptors = new HashMap<>();

		for (PropertyDescriptor pd : Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors()) {
			propertyDescriptors.put(pd.getName(), pd);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int compare(T o1, T o2) {
		for (int i = 0; i < propertyId.length && i < ascending.length; i++) {
			PropertyDescriptor pd = propertyDescriptors.get(propertyId[i]);
			if (pd == null) {
				continue;
			}

			try {
				Object property1 = propertyDescriptors.get(propertyId[i]).getReadMethod().invoke(o1);
				Object property2 = propertyDescriptors.get(propertyId[i]).getReadMethod().invoke(o2);
				if (String.class.isAssignableFrom(property1.getClass())){
					int result = ((String) property1).compareToIgnoreCase((String)property2);
					if (result == 0) {
						continue;
					} else {
						if (ascending[i]) {
							return result;
						} else {
							if (result > 0) {
								return -1;
							} else {
								return 1;
							}
						}
					}
				}				
				if (Comparable.class.isAssignableFrom(property1.getClass())) {
					@SuppressWarnings("unchecked")
					int result = ((Comparable) property1).compareTo(property2);
					if (result == 0) {
						continue;
					} else {
						if (ascending[i]) {
							return result;
						} else {
							if (result > 0) {
								return -1;
							} else {
								return 1;
							}
						}
					}
				}

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// Ignore and proceed
				StorableObjectLogger.errorMessage(ReflectionComparator.class.getName(), e);
			}

		}
		return 0;
	}
}
