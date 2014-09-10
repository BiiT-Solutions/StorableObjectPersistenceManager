package com.biit.persistence.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.biit.persistence.utils.IdGenerator;
import com.liferay.portal.model.User;

@Entity
@Table(name = "storable_objects")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class StorableObject {
	public final static int MAX_UNIQUE_COLUMN_LENGTH = 190;

	// GenerationType.Table stores into hibernate_sequence the name of the table
	// as a VARCHAR(255) when using
	// "hibernate.id.new_generator_mappings" property. If using utf8mb4, the
	// VARCHAR(255) needs 1000 bytes, that this is
	// forbidden due to MySQL only allows a max of 767 bytes in a unique key. If
	// "hibernate.id.new_generator_mappings"
	// is not set, GenerationType.AUTO causes Cannot use identity column key
	// generation with <union-subclass> error.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	private Long id;

	@Column(nullable = false)
	private Timestamp creationTime = null;
	@Column(columnDefinition = "DOUBLE")
	private Long createdBy = null;
	private Timestamp updateTime = null;
	@Column(columnDefinition = "DOUBLE")
	private Long updatedBy = null;

	// A unique Id created with the object used to compare persisted objects and
	// in memory objects.
	// MySQL unique keys are limited to 767 bytes that in utf8mb4 are ~190.
	@Column(unique = true, nullable = false, updatable = false, length = MAX_UNIQUE_COLUMN_LENGTH)
	private String comparationId;

	public StorableObject() {
		creationTime = new java.sql.Timestamp(new java.util.Date().getTime());
		comparationId = IdGenerator.createId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public Timestamp getCreationTime() {
		if (creationTime != null) {
			return creationTime;
		} else {
			creationTime = new java.sql.Timestamp(new java.util.Date().getTime());
			return creationTime;
		}
	}

	public void setUpdateTime() {
		setUpdateTime(new java.sql.Timestamp(new java.util.Date().getTime()));
	}

	public Timestamp getUpdateTime() {
		if (updateTime != null) {
			return updateTime;
		} else {
			updateTime = new java.sql.Timestamp(new java.util.Date().getTime());
			return updateTime;
		}
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedBy(User user) {
		if (user != null) {
			createdBy = user.getUserId();
		}
	}

	public void setCreationTime(Timestamp dateCreated) {
		creationTime = dateCreated;
	}

	public void setUpdateTime(Timestamp dateUpdated) {
		updateTime = dateUpdated;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public void setUpdatedBy(User user) {
		if (user != null) {
			updatedBy = user.getUserId();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((comparationId == null) ? 0 : comparationId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		StorableObject other = (StorableObject) obj;
		if (comparationId == null) {
			if (other.comparationId != null) {
				return false;
			}
		} else if (!comparationId.equals(other.comparationId)) {
			return false;
		}
		return true;
	}

	public void resetIds() {
		setId(null);
	}

	public synchronized String getComparationId() {
		return comparationId;
	}

	/**
	 * Needed for the drools engine <br>
	 * The identifiers in drools can't contain dashes so they are eliminated before returning the string
	 * 
	 * @return
	 */
	public synchronized String getComparationIdNoDash() {
		return comparationId.replaceAll("-", "");
	}

}
