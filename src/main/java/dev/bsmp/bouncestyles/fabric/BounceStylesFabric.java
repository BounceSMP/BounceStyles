//? if fabric {
package dev.bsmp.bouncestyles.fabric;

import com.mojang.serialization.Lifecycle;
import dev.architectury.platform.Platform;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.fabric.compat.FlashbackCompat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public class BounceStylesFabric implements ModInitializer {
    public static final AttachmentType<StyleData> STYLE_DATA_ATTACHMENT = AttachmentRegistry.<StyleData>builder()
            .initializer(StyleData::new)
            .persistent(StyleData.CODEC)
            .buildAndRegister(BounceStyles.id("style_data"));

    @Override
    public void onInitialize() {
        BounceStyles.init();
        DynamicRegistries.registerSynced(BounceStylesRegistries.STYLE_REGISTRY_KEY, Style.CODEC.withLifecycle(Lifecycle.stable()));
        if (Platform.isModLoaded("flashback"))
            FlashbackCompat.init();
    }

}
//?}