//? if forge || neoforge {
package dev.bsmp.bouncestyles.mixin.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.core.BounceStyles;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

//? if >= 1.21.11 {
import top.theillusivec4.curios.client.CuriosLayer;
//? } else
//import top.theillusivec4.curios.client.render.CuriosLayer;

@Mixin(CuriosLayer.class)
//? if >= 1.21.11 {
public abstract class CuriosRenderingMixin<S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
//? } else
//public abstract class CuriosRenderingMixin<T extends LivingEntity, M extends EntityModel<T>> {

    //? if >= 1.21.11 {
    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Ltop/theillusivec4/curios/api/client/ICurioRenderer;render(Lnet/minecraft/world/item/ItemStack;Ltop/theillusivec4/curios/api/SlotContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lnet/minecraft/client/renderer/entity/RenderLayerParent;Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;FF)V"))
    private void bounceStyles$shouldRender(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, S renderState, float vertRot, float horizRot, CallbackInfo ci) {
        //ToDo Curios slot hiding support like trinkets
    }
    //? } else {
//    @Inject(method = "lambda$render$1", at = @At("HEAD"))
//    private void bounceStyles$shouldRender(LivingEntity livingEntity, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, String id, ICurioStacksHandler stacksHandler, CallbackInfo ci) {
//    }
    //? }

}
//?}