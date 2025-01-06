package com.ewyboy.blockexchange.events;

import com.ewyboy.blockexchange.database.services.DatabaseService;
import com.ewyboy.blockexchange.database.entities.DbPlayer;
import com.ewyboy.blockexchange.database.services.DatabaseServiceRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

import java.time.LocalDateTime;

public class PlayerEventHandler {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            DatabaseService<DbPlayer> playerService = DatabaseServiceRegistry.getService(DbPlayer.class);

            DbPlayer dbPlayer = new DbPlayer();

            dbPlayer.setUuid(player.getUUID());
            dbPlayer.setUsername(player.getName().getString());
            dbPlayer.setLastLogin(LocalDateTime.now());
            dbPlayer.setUpdatedAt(LocalDateTime.now());

            playerService.saveOrUpdate(dbPlayer);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            DatabaseService<DbPlayer> playerService = DatabaseServiceRegistry.getService(DbPlayer.class);

            DbPlayer dbPlayer = playerService.findById(player.getUUID());

            if (dbPlayer != null) {
                dbPlayer.setLastLogout(LocalDateTime.now());
                dbPlayer.setUpdatedAt(LocalDateTime.now());
                playerService.saveOrUpdate(dbPlayer);
            }
        });
    }

}
