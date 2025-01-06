package com.ewyboy.blockexchange.database;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Objects;

public class DatabaseService {

    public void saveOrUpdateEntity(Object entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            if (Objects.isNull(entity)) {
                saveEntity(entity, session, transaction);
            } else {
                updateEntity(entity, session, transaction);
            }
        } finally {
            session.close();
        }
    }

    private void saveEntity(Object entity, Session session, Transaction transaction) {
        try {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    private void updateEntity(Object entity, Session session, Transaction transaction) {
        try {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Object getEntityById(Class<?> clazz, long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        Object entity = null;

        try {
            transaction = session.beginTransaction();
            entity = session.get(clazz, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return entity;
    }

    public Object getEntityByUUID(Class<?> clazz, String uuid) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        Object entity = null;

        try {
            transaction = session.beginTransaction();
            entity = session.get(clazz, uuid);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return entity;
    }

}
