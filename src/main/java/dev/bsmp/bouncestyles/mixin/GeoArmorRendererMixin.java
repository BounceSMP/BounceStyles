package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.item.StyleItem;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Mixin(value = GeoArmorRenderer.class, remap = false)
public abstract class GeoArmorRendererMixin<T extends ArmorItem & IAnimatable> {

    @Shadow public abstract AnimatedGeoModel<T> getGeoModelProvider();

    @Inject(method = "apply(Lsoftware/bernie/geckolib3/core/IAnimatable;)Lsoftware/bernie/geckolib3/core/IAnimatableModel;", at = @At("HEAD"), cancellable = true)
    private void isStyleItem(IAnimatable t, CallbackInfoReturnable ci) {
        if(t instanceof StyleItem) {
            ci.setReturnValue(this.getGeoModelProvider());
        }
    }

}
