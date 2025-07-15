//? if forge || neoforge {
/*package dev.bsmp.bouncestyles.mixin.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.core.BounceStyles;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.client.render.CuriosLayer;

@Mixin(CuriosLayer.class)
public abstract class CuriosRenderingMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(method = "lambda$render$1", at = @At("HEAD"))
    private void bounceStyles$shouldRender(LivingEntity livingEntity, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, String id, ICurioStacksHandler stacksHandler, CallbackInfo ci) {
        BounceStyles.LOGGER.info(id);
    }

}
*///?}