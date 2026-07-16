package dev.bsmp.bouncestyles.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.bsmp.bouncestyles.core.client.Keybinds;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class KeyMappingMixin {

    @Inject(method = "click", at = @At("TAIL"))
    private static void bounceStyles$handlePresetKeybinds(InputConstants.Key key, CallbackInfo ci) {
        Keybinds.handleKeys(key);
    }

}
