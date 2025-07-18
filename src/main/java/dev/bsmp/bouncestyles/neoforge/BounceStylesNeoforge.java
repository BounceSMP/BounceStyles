//? if neoforge {
/*package dev.bsmp.bouncestyles.neoforge;

import com.mojang.serialization.Lifecycle;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@Mod(BounceStyles.modId)
@EventBusSubscriber(modid = BounceStyles.modId, bus = EventBusSubscriber.Bus.MOD)
public class BounceStylesNeoforge<T extends StylePacket> {
    public BounceStylesNeoforge() {
        BounceStyles.init();
    }

    @SubscribeEvent
    public static void registerDynamicRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BounceStylesRegistries.STYLE_REGISTRY_KEY, Style.CODEC.withLifecycle(Lifecycle.stable()), Style.CODEC.withLifecycle(Lifecycle.stable()));
    }
}
*///?}