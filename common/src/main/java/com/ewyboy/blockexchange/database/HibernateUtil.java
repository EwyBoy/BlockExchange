package com.ewyboy.blockexchange.database;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Load configuration explicitly to catch issues early
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");

            // Add annotated classes to configuration
            configuration.addAnnotatedClass(com.ewyboy.blockexchange.database.entities.DbItem.class);
            configuration.addAnnotatedClass(com.ewyboy.blockexchange.database.entities.DbPlayer.class);
            configuration.addAnnotatedClass(com.ewyboy.blockexchange.database.entities.DbOrder.class);
            configuration.addAnnotatedClass(com.ewyboy.blockexchange.database.entities.DbTransaction.class);

            // Manually build service registry to ensure all services load correctly
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
