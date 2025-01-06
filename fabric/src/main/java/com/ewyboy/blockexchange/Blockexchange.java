package com.ewyboy.blockexchange;

import com.ewyboy.blockexchange.commands.OrderCommand;
import com.ewyboy.blockexchange.events.PlayerEventHandler;
import net.fabricmc.api.ModInitializer;

public class Blockexchange implements ModInitializer {

    @Override
    public void onInitialize() {

        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();

        PlayerEventHandler.register();
        OrderCommand.register();
    }
}
