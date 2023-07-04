package com.biit.persistence.dao.hibernate;

import com.biit.persistence.dao.IGenericDao;
import com.biit.persistence.dao.exceptions.ElementCannotBePersistedException;
import com.biit.persistence.dao.exceptions.UnexpectedEntityDatabaseException;
import com.biit.persistence.entity.StorableObject;
import com.biit.persistence.entity.exceptions.ElementCannotBeRemovedException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GenericDao<T extends StorableObject> extends StorableObjectDao<T> implements IGenericDao<T> {
    // Recommended values are [15-25]. Bigger values reduce database access but
    // increase CPU consumption.
    private static final int MAX_OBJETS_PER_SESSION = 25;

    private Class<T> type;

    public GenericDao(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    /**
     * Get all elements that has a null value in the ID parameter before
     * persisting.
     *
     * @param entity
     * @return
     */
    public Set<StorableObject> getElementsWithNullIds(T entity) {
        final Set<StorableObject> elementsWithNullIds = new HashSet<>();
        if (entity.getId() == null) {
            elementsWithNullIds.add(entity);
        }
        if (entity.getAllInnerStorableObjects() != null) {
            for (StorableObject child : entity.getAllInnerStorableObjects()) {
                if (child.getId() == null) {
                    elementsWithNullIds.add(child);
                }
            }
        }
        return elementsWithNullIds;
    }

    /**
     * Get all elements that has a null value in the ID parameter before
     * persisting.
     *
     * @param entities
     * @return
     */
    public Set<StorableObject> getElementsWithNullIds(Set<StorableObject> entities) {
        final Set<StorableObject> elementsWithNullIds = new HashSet<>();
        for (StorableObject entity : entities) {
            if (entity.getId() == null) {
                elementsWithNullIds.add(entity);
            }
        }
        return elementsWithNullIds;
    }

    /**
     * Rest the Ids of this elements.
     *
     * @param elementsThatMustHaveNullId
     */
    public void setNullIds(Set<StorableObject> elementsThatMustHaveNullId) {
        for (StorableObject element : elementsThatMustHaveNullId) {
            element.setId(null);
        }
    }

    @Override
    public T makePersistent(T entity) throws UnexpectedEntityDatabaseException, ElementCannotBePersistedException {
        setCreationInfo(entity);
        setUpdateInfo(entity);
        final Set<StorableObject> elementsWithNullIds = getElementsWithNullIds(entity);
        final Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate(entity);
            session.flush();
            session.getTransaction().commit();
            return entity;
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            // Reset the IDs if hibernate has put a value before rollback.
            setNullIds(elementsWithNullIds);
            throw new UnexpectedEntityDatabaseException(e.getMessage(), e);
        }
    }

    @Override
    public List<T> makePersistent(List<T> entities) throws UnexpectedEntityDatabaseException {
        final Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        int objectsToStore = 0;
        try {
            for (int i = 0; i < entities.size(); i++) {
                setCreationInfo(entities.get(i));
                setUpdateInfo(entities.get(i));
                session.saveOrUpdate(entities.get(i));
                objectsToStore++;

                if (objectsToStore > MAX_OBJETS_PER_SESSION || i == entities.size() - 1) {
                    session.flush();
                    session.clear();
                    objectsToStore = 0;
                }
            }
            session.getTransaction().commit();
            return entities;
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            throw new UnexpectedEntityDatabaseException(e.getMessage(), e);
        }
    }

    public void removeStorableObject(StorableObject entity) throws UnexpectedEntityDatabaseException {
        final Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            session.delete(entity);
            session.flush();
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            throw new UnexpectedEntityDatabaseException(e.getMessage(), e);
        }
    }

    @Override
    public T read(Long id) throws UnexpectedEntityDatabaseException {
        final Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            @SuppressWarnings("unchecked") final T object = (T) session.get(getType(), id);
            initializeSet(object);
            session.getTransaction().commit();
            return object;
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            throw new UnexpectedEntityDatabaseException(e.getMessage(), e);
        }
    }

    @Override
    public int getRowCount() throws UnexpectedEntityDatabaseException {
        final Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            // Create CriteriaBuilder
            final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            final Root<T> root = criteriaQuery.from(getType());

            criteriaQuery.select(criteriaBuilder.count(root));
            return session.createQuery(criteriaQuery).getSingleResult().intValue();
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            throw new UnexpectedEntityDatabaseException(e.getMessage(), e);
        }
    }

    @Override
    public List<T> getAll() throws UnexpectedEntityDatabaseException {
        final Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            // session.createCriteria(getType()).list() is not working returns
            // repeated elements due to
            // http://stackoverflow.com/questions/8758363/why-session-createcriteriaclasstype-list-return-more-object-than-in-list
            // if we have a list with eager fetch.
            final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getType());
            final Root<T> root = criteriaQuery.from(getType());
            criteriaQuery.select(criteriaQuery.from(getType()));
            try {
                return session.createQuery(criteriaQuery).getResultList();
            } catch (NoResultException var3) {
                return new ArrayList<>();
            }
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            throw new UnexpectedEntityDatabaseException(e.getMessage(), e);
        }
    }

    /**
     * Truncates the table.
     *
     * @throws UnexpectedEntityDatabaseException
     */
    @Override
    public void removeAll() throws UnexpectedEntityDatabaseException {
        final List<T> elements = getAll();
        for (T element : elements) {
            deleteStorableObject(element);
        }
    }

    /**
     * When using lazy loading, the sets must have a proxy to avoid a
     * "LazyInitializationException: failed to lazily initialize a collection of..."
     * error. This procedure must be called before closing the session.
     *
     * @param element
     */
    private void initializeSet(T element) {
        if (element != null) {
            final List<T> elements = new ArrayList<>();
            elements.add(element);
            initializeSets(elements);
        }
    }

    /**
     * When using lazy loading, the sets must have a proxy to avoid a
     * "LazyInitializationException: failed to lazily initialize a collection of..."
     * error. This procedure must be called before closing the session.
     *
     * @param elements
     */
    protected abstract void initializeSets(List<T> elements);

    @Override
    public void evictAllCache() {
        if (getSessionFactory() != null && getSessionFactory().getCache() != null) {
            getSessionFactory().getCache().evictAllRegions();
        }
    }

    @Override
    public void makeTransient(T entity) throws UnexpectedEntityDatabaseException, ElementCannotBeRemovedException {
        if (entity != null) {
            super.deleteStorableObject(entity);
        }
    }
}
