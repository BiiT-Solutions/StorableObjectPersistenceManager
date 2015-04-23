package com.biit.persistence.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;

import com.biit.persistence.entity.exceptions.NotValidStorableObjectException;

/**
 * Storable object inherits all the methods and data from base storable object
 * and adds a set of methods to allow the copying of the object. This type of
 * methods are not always required so only those storable objects that require
 * copying should extend this class.
 * 
 * It also has a method for drools that probably should be removed.
 *
 */
@Entity
public abstract class StorableObject extends BaseStorableObject implements
		Serializable {
	private static final long serialVersionUID = 1254938842002423347L;

	/**
	 * Needed for the drools engine <br>
	 * The identifiers in drools can't contain dashes so they are eliminated
	 * before returning the string
	 * 
	 * @return
	 */
	public synchronized String getUniqueNameReadable() {
		return getComparationId().replaceAll("-", "");
	}

	/**
	 * Return all inner elements that compose this object.
	 * 
	 * @return
	 */
	public abstract Set<StorableObject> getAllInnerStorableObjects();

	/**
	 * Function to copy internal data of each different class that inherits
	 * treeObject.
	 * 
	 * @param object
	 */
	public abstract void copyData(StorableObject object)
			throws NotValidStorableObjectException;

	protected void copyBasicInfo(StorableObject object)
			throws NotValidStorableObjectException {
		setCreatedBy(object.getCreatedBy());
		setUpdatedBy(object.getUpdatedBy());
		setId(object.getId());
		setComparationId(object.getComparationId());
		setCreationTime(object.getCreationTime());
		setUpdateTime(object.getUpdateTime());
	}

}
