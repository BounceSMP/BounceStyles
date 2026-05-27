//? if fabric {
package dev.bsmp.bouncestyles.fabric;

import com.mojang.serialization.Lifecycle;
import dev.bsmp.bouncestyles.core.data.style.Style;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public class BounceStylesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BounceStyles.init();
        DynamicRegistries.registerSynced(BounceStylesRegistries.STYLE_REGISTRY_KEY, Style.CODEC.withLifecycle(Lifecycle.stable()));
    }

}
//?}