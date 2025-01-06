package com.ewyboy.blockexchange.events;

import com.ewyboy.blockexchange.database.DatabaseService;
import com.ewyboy.blockexchange.database.PlayerService;
import com.ewyboy.blockexchange.database.entities.DBPlayer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

import java.time.LocalDateTime;

public class PlayerEventHandler {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            DatabaseService service = new DatabaseService();

            DBPlayer dbPlayer = new DBPlayer();

            LocalDateTime localDateTime = LocalDateTime.now();

            dbPlayer.setUuid(player.getUUID());
            dbPlayer.setUsername(player.getName().getString());

            dbPlayer.setLastLogin(localDateTime);
            dbPlayer.setUpdatedAt(localDateTime);

            service.saveOrUpdateEntity(dbPlayer);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            DatabaseService databaseService = new DatabaseService();
            PlayerService playerService = new PlayerService();

            DBPlayer dbPlayer = playerService.getPlayerByUUID(player.getUUID());

            if (dbPlayer != null) {
                dbPlayer.setLastLogout(LocalDateTime.now());
                dbPlayer.setUpdatedAt(LocalDateTime.now());
                databaseService.saveOrUpdateEntity(dbPlayer);
            }
        });
    }

}
