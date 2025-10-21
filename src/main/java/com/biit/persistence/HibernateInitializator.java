package com.biit.persistence;

/*-
 * #%L
 * Form Based Generic Persistence Manager
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
