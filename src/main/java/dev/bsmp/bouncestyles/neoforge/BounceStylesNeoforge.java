//? if neoforge {
/*package dev.bsmp.bouncestyles.neoforge;

import com.mojang.serialization.Lifecycle;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.data.style.Style;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@Mod(BounceStyles.modId)
@EventBusSubscriber(modid = BounceStyles.modId)
public class BounceStylesNeoforge {
    public BounceStylesNeoforge() {
        BounceStyles.init();
    }

    @SubscribeEvent
    public static void registerDynamicRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BounceStylesRegistries.STYLE_REGISTRY_KEY, Style.CODEC.withLifecycle(Lifecycle.stable()), Style.CODEC.withLifecycle(Lifecycle.stable()));
    }
}
*///?}