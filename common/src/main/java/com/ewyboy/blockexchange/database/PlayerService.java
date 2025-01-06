package com.ewyboy.blockexchange.database;

import com.ewyboy.blockexchange.database.entities.DBPlayer;
import org.hibernate.Session;

import java.util.UUID;

public class PlayerService {

    public DBPlayer getPlayerByUUID(UUID uuid) {

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            return session.get(DBPlayer.class, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return null;
    }

}
