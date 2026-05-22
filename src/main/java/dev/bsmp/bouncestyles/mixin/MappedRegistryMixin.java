package dev.bsmp.bouncestyles.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Lifecycle;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {
    @Shadow public abstract ResourceKey<? extends Registry<T>> key();

    @Shadow private Lifecycle registryLifecycle;

    //? if >= 1.21.1 {
    @ModifyReturnValue(method = "registryLifecycle", at = @At("RETURN"))
    private Lifecycle bounceStyles$markStable(Lifecycle original) {
        if (this.key().equals(BounceStylesRegistries.STYLE_REGISTRY_KEY) && original != Lifecycle.stable())
            this.registryLifecycle = Lifecycle.stable();
        return original;
    }
    //?}
}
