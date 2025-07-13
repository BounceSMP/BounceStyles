package dev.bsmp.bouncestyles.mixin;

import com.mojang.serialization.Lifecycle;
import dev.bsmp.bouncestyles.core.BounceStyles;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {

//    @Redirect(method = "register", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/RegistrationInfo;lifecycle()Lcom/mojang/serialization/Lifecycle;"))
//    private Lifecycle bounceStyles$markStable(RegistrationInfo instance, @Local(argsOnly = true) ResourceKey<T> key) {
//        if (key.registry().getNamespace().equals(BounceStyles.modId)) return Lifecycle.stable();
//        return instance.lifecycle();
//    }

}
