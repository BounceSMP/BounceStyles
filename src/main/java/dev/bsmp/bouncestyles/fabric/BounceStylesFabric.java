//? if fabric {
package dev.bsmp.bouncestyles.fabric;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.data.Style;
import dev.bsmp.bouncestyles.core.pack.StylePackProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public class BounceStylesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BounceStyles.init();
        ServerLifecycleEvents.SERVER_STARTED.register(StylePackProvider::registerToDataPacks);
        DynamicRegistries.registerSynced(BounceStylesRegistries.STYLE_REGISTRY_KEY, Style.CODEC);
    }

}
//?}