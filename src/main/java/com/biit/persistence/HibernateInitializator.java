package com.biit.persistence;

import com.biit.persistence.logger.StorableObjectLogger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * This is the utility class for getting SessionFactory
 */
public final class HibernateInitializator {

    private static SessionFactory sessionFactory = buildSessionFactory();
    private static Configuration configuration;

    private HibernateInitializator() {
        // Private constructor to hide the implicit public one.
    }

    private static SessionFactory buildSessionFactory() {
        try {
            configuration = new Configuration();
            configuration.configure();
            final StandardServiceRegistryBuilder sb = new StandardServiceRegistryBuilder();
            sb.applySettings(configuration.getProperties());
            final StandardServiceRegistry standardServiceRegistry = sb.build();
            sessionFactory = configuration.buildSessionFactory(standardServiceRegistry);
            return sessionFactory;
        } catch (HibernateException ex) {
            StorableObjectLogger.errorMessage(HibernateInitializator.class.getName(), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
