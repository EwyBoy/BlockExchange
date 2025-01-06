package com.ewyboy.blockexchange.database.services;

import com.ewyboy.blockexchange.database.entities.DbItem;
import com.ewyboy.blockexchange.database.entities.DbOrder;
import com.ewyboy.blockexchange.database.entities.DbPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry for all database services
 * <p>
 * This class is used to store all database services in a map.
 * This allows for easy access to the services from anywhere in the code.
 * The services are stored in a map with the class of the entity as the key and the service as the value.
 * This allows for easy access to the service for a specific entity.
 * The services are stored in a static map so they can be accessed from anywhere in the code.
 * The services are stored in a map with the class of the entity as the key and the service as the value.
 * </p>
 */
public class DatabaseServiceRegistry {

    private static final Map<Class<?>, DatabaseService<?>> services = new HashMap<>();

    static {
        services.put(DbOrder.class, new DatabaseService<>(DbOrder.class));
        services.put(DbPlayer.class, new DatabaseService<>(DbPlayer.class));
        services.put(DbItem.class, new DatabaseService<>(DbItem.class));
    }

    @SuppressWarnings("unchecked")
    public static <T> DatabaseService<T> getService(Class<T> clazz) {
        return (DatabaseService<T>) services.get(clazz);
    }

}
