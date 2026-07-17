//? if neoforge {
/*package dev.bsmp.bouncestyles.neoforge;

import com.mojang.serialization.Lifecycle;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.api.style.Style;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@Mod(BounceStyles.modId)
public class BounceStylesNeoforge {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BounceStyles.modId);
    public static final Supplier<AttachmentType<StyleData>> STYLE_DATA_ATTACHMENT = ATTACHMENT_TYPES.register("style_data", () ->
        AttachmentType.builder(StyleData::new)
                //? if >= 1.21.10 {
                .serialize(StyleData.CODEC.fieldOf("style_data"))
                //? } else
                //.serialize(StyleData.CODEC.fieldOf("style_data").codec())
                .build()
    );

    public BounceStylesNeoforge(IEventBus modBus) {
        BounceStyles.init();

        modBus.addListener(BounceStylesNeoforge::registerDynamicRegistries);

        ATTACHMENT_TYPES.register(modBus);
    }

    public static void registerDynamicRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BounceStylesRegistries.STYLE_REGISTRY_KEY, Style.CODEC.withLifecycle(Lifecycle.stable()), Style.CODEC.withLifecycle(Lifecycle.stable()));
    }
}
*///?}