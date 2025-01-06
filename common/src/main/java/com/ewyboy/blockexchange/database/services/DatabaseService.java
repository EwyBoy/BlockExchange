package com.ewyboy.blockexchange.database.services;

import com.ewyboy.blockexchange.database.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.List;

public class DatabaseService<T> {

    private final Class<T> entityType;

    public DatabaseService(Class<T> entityType) {
        this.entityType = entityType;
    }

    public void saveOrUpdate(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void saveAll(List<T> entities) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            for (int i = 0; i < entities.size(); i++) {
                session.merge(entities.get(i));
                // Flush and clear periodically to avoid memory issues
                if (i % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public T findById(Serializable id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.get(entityType, id);
        } finally {
            session.close();
        }
    }

    public List<T> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.createQuery("from " + entityType.getName(), entityType).getResultList();
        } finally {
            session.close();
        }
    }

    public void delete(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteById(Serializable id) {
        T entity = findById(id);
        if (entity != null) {
            delete(entity);
        }
    }
}