package com.ewyboy.blockexchange.events;

import com.ewyboy.blockexchange.database.DatabaseService;
import com.ewyboy.blockexchange.database.entities.DBPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.time.LocalDateTime;

@EventBusSubscriber
public class PlayerEventHandler {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        DatabaseService service = new DatabaseService();

        DBPlayer dbPlayer = new DBPlayer();

        LocalDateTime localDateTime = LocalDateTime.now();

        dbPlayer.setUuid(player.getUUID());
        dbPlayer.setUsername(player.getName().getString());

        dbPlayer.setLastLogin(localDateTime);
        dbPlayer.setCreatedAt(localDateTime);
        dbPlayer.setUpdatedAt(localDateTime);

        service.saveOrUpdateEntity(dbPlayer);
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        DatabaseService service = new DatabaseService();

        Object dbPlayer = service.getEntityByUUID(DBPlayer.class, player.getUUID().toString());

        if (dbPlayer instanceof DBPlayer playerEntity) {
            playerEntity.setLastLogout(LocalDateTime.now());
            service.saveOrUpdateEntity(playerEntity);
        }
    }

}
