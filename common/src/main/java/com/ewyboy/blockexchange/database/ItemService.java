package com.ewyboy.blockexchange.database;

import com.ewyboy.blockexchange.database.entities.DBItem;
import org.hibernate.Session;

public class ItemService {

    public DBItem getItem(String resourceLocation) {

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            return session.get(DBItem.class, resourceLocation);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return null;
    }

}
