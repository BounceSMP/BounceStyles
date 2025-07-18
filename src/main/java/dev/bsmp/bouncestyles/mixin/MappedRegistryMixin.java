package dev.bsmp.bouncestyles.mixin;

import com.mojang.serialization.Lifecycle;
import dev.bsmp.bouncestyles.core.BounceStyles;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {
    //? if >= 1.21.1 {
    @Shadow private Lifecycle registryLifecycle;

    @Inject(method = "register", at = @At(value = "RETURN"))
    private void bounceStyles$markStable(ResourceKey<T> key, T value, net.minecraft.core.RegistrationInfo registrationInfo, CallbackInfoReturnable<Holder.Reference<T>> cir) {
        if (key.registry().getNamespace().equals(BounceStyles.modId))
            this.registryLifecycle = Lifecycle.stable();
    }
    //?}
}
