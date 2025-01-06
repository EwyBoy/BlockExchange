package com.ewyboy.blockexchange;

import com.ewyboy.blockexchange.database.services.DatabaseService;
import com.ewyboy.blockexchange.database.entities.DbItem;
import com.ewyboy.blockexchange.database.services.DatabaseServiceRegistry;
import com.ewyboy.blockexchange.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommonClass {

    public static void init() {
        if (Services.PLATFORM.isModLoaded("blockexchange")) {
            Constants.LOG.info("Hello to blockexchange");
        }

        DatabaseService<DbItem> itemService = DatabaseServiceRegistry.getService(DbItem.class);
        List<DbItem> existingItems = itemService.findAll();
        List<DbItem> itemsToSaveOrUpdate = new ArrayList<>();
        Map<String, DbItem> existingItemMap = existingItems
                .stream()
                .collect(Collectors.toMap(DbItem::getResourceLocation, item -> item));


        BuiltInRegistries.ITEM.forEach(item -> {
            String resourceLocation = BuiltInRegistries.ITEM.getKey(item).toString();

            DbItem existingItem = existingItemMap.get(resourceLocation);

            if (existingItem == null) {
                DbItem newItem = new DbItem();

                newItem.setNamespace(BuiltInRegistries.ITEM.getKey(item).getNamespace());
                newItem.setPath(BuiltInRegistries.ITEM.getKey(item).getPath());
                newItem.setResourceLocation(resourceLocation);

                itemsToSaveOrUpdate.add(newItem);
            }
        });

        if (!itemsToSaveOrUpdate.isEmpty()) {
            itemService.saveAll(itemsToSaveOrUpdate);
            Constants.LOG.info("Added {} new items to the database.", itemsToSaveOrUpdate.size());
        } else {
            Constants.LOG.info("No new items to add to the database.");
        }

    }
}
