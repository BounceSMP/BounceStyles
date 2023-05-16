package dev.bsmp.bouncestyles.fabric;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.pack.StylePackProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class BounceStylesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BounceStyles.init();
        ServerLifecycleEvents.SERVER_STARTED.register(StylePackProvider::registerToDataPacks);
    }

}
