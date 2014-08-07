package com.biit.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * This is the utility class for getting SessionFactory
 */
public class HibernateInitializator {

	private static SessionFactory sessionFactory = buildSessionFactory();
	private static Configuration configuration;

	private static SessionFactory buildSessionFactory() {
		try {
			configuration = new Configuration();
			configuration.configure();
			StandardServiceRegistryBuilder sb = new StandardServiceRegistryBuilder();
			sb.applySettings(configuration.getProperties());
			StandardServiceRegistry standardServiceRegistry = sb.build();
			sessionFactory = configuration.buildSessionFactory(standardServiceRegistry);
			return sessionFactory;
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
