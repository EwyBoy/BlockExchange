package com.ewyboy.blockexchange;

import com.ewyboy.blockexchange.client.ClientEvents;
import com.ewyboy.blockexchange.client.KeyMappingHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class BlockexchangeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyMappingHandler.register(KeyBindingHelper::registerKeyBinding);
        ClientEvents.register();
    }
}
