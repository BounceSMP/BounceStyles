package dev.bsmp.bouncestyles.fabric;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.networking.packets.SyncStyleDataClientbound;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;

public class BounceStylesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BounceStyles.init();
    }

}
