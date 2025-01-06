package com.ewyboy.blockexchange.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class KeyMappingHandler {

    public static KeyMapping openExchangeScreen;

    public static void register(Consumer<KeyMapping> mappingConsumer) {
        mappingConsumer.accept(openExchangeScreen = new KeyMapping("key.blockexchange.open_exchange_screen", InputConstants.KEY_PAUSE, "key.categories.blockexchange"));
    }

    public static void consumeClick() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) {
            return;
        }

        if (openExchangeScreen.consumeClick()) {
            minecraft.setScreen(new ExchangeScreen(Component.literal("Block Exchange")));
        }
    }

}
