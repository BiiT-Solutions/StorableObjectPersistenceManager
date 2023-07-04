package com.biit.persistence.entity;

import com.biit.persistence.utils.IdGenerator;
import com.biit.usermanager.entity.IUser;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Base StorableObject class. This class holds all the basic storable object
 * information doesn't have the copy data methods and thus doesn't require that
 * it's child classes implement it. This base class is used as in the USMO
 * project.
 */
// If marked as @MappedSuperclass, in database is added an auto_increment for
// all subclasses. But, the cache handler can handle this in their own cache
// region and not in BaseStorable region. Marked as @MappedSuperclass disabled
// to errors in usmo-integration
@MappedSuperclass
// @Cacheable and @Cache not needed for caching if marked as @MappedSuperclass
// @Cacheable(true)
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseStorableObject implements Serializable {
    private static final long serialVersionUID = 1861734314986978986L;
    public static final int MAX_UNIQUE_COLUMN_LENGTH = 190;
    public static final int HASH_CODE_SEED = 31;

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
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(nullable = false, name = "creation_time")
    private Timestamp creationTime = null;
    @Column(columnDefinition = "DOUBLE", name = "created_by")
    private Long createdBy = null;
    @Column(name = "update_time")
    private Timestamp updateTime = null;
    @Column(columnDefinition = "DOUBLE", name = "updated_by")
    private Long updatedBy = null;

    // A unique Id created with the object used to compare persisted objects and
    // in memory objects.
    // MySQL unique keys are limited to 767 bytes that in utf8mb4 are ~190.
    @Column(name = "comparation_id", unique = true, nullable = false, updatable = false, length = MAX_UNIQUE_COLUMN_LENGTH)
    private String comparationId;

    public BaseStorableObject() {
        setCreationTime(new java.sql.Timestamp(new java.util.Date().getTime()));
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

    public void setCreationTime() {
        setCreationTime(new java.sql.Timestamp(new Date().getTime()));
    }

    public Timestamp getCreationTime() {
        if (creationTime != null) {
            return creationTime;
        } else {
            setCreationTime(new java.sql.Timestamp(getRoundedMilliseconds(new Date())));
            return creationTime;
        }
    }

    public void setUpdateTime() {
        setUpdateTime(new java.sql.Timestamp(new Date().getTime()));
    }

    private long getRoundedMilliseconds(Date date) {
        if (date == null) {
            return 0;
        }
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public Timestamp getUpdateTime() {
        if (updateTime != null) {
            return updateTime;
        } else {
            updateTime = new java.sql.Timestamp(getRoundedMilliseconds(new Date()));
            return updateTime;
        }
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    @JsonSetter
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedBy(IUser<Long> user) {
        if (user != null) {
            createdBy = user.getUniqueId();
        }
    }

    public void setCreationTime(Timestamp dateCreated) {
        creationTime = new Timestamp(getRoundedMilliseconds(dateCreated));
    }

    public void setUpdateTime(Timestamp dateUpdated) {
        updateTime = new Timestamp(getRoundedMilliseconds(dateUpdated));
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setUpdatedBy(IUser<Long> user) {
        if (user != null) {
            updatedBy = user.getUniqueId();
        }
    }

    @Override
    public int hashCode() {
        final int prime = HASH_CODE_SEED;
        int result = 1;
        result = (prime * result) + ((getComparationId() == null) ? 0 : getComparationId().hashCode());
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
        if (!this.getClass().isInstance(obj)) {
            return false;
        }

        final BaseStorableObject other = (BaseStorableObject) obj;
        if (getComparationId() == null) {
            if (other.getComparationId() != null) {
                return false;
            }
        } else if (!getComparationId().equals(other.getComparationId())) {
            return false;
        }
        return true;
    }

    /**
     * Reset 'id' and 'comparationId'
     */
    public void resetIds() {
        setId(null);
        comparationId = IdGenerator.createId();
    }

    /**
     * This method must not be used directly.
     *
     * @param comparationId
     */
    public synchronized void setComparationId(String comparationId) {
        this.comparationId = comparationId;
    }

    public synchronized String getComparationId() {
        return comparationId;
    }

}
