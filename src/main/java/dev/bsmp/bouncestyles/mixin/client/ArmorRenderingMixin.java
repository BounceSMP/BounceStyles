package dev.bsmp.bouncestyles.mixin.client;
//~ avatar

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if >= 1.21.5 {
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import com.llamalad7.mixinextras.sugar.Local;
import dev.bsmp.bouncestyles.core.client.renderer.StyleDataTickets;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;
//? } else {
/*import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Avatar;
import net.minecraft.client.renderer.MultiBufferSource;
*///? }

@Mixin(value = HumanoidArmorLayer.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
//? if >= 1.21.11 {
public abstract class ArmorRenderingMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> {
//? } else
//public class ArmorRenderingMixin<T extends LivingEntity, A extends HumanoidModel<T>> {

    //? if >= 1.21.11 {
    @Shadow
    protected abstract void renderArmorPiece(PoseStack poseStack, SubmitNodeCollector nodeCollector, ItemStack item, EquipmentSlot slot, int packedLight, S renderState);

    @Redirect(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V")
    )
    private void bounceStyles$skipArmorRendering(HumanoidArmorLayer instance, PoseStack poseStack, SubmitNodeCollector nodeCollector, ItemStack item, EquipmentSlot slot, int packedLight, S renderState, @Local S humanoidRenderState) {
        if (humanoidRenderState instanceof GeoRenderState state) {
            StyleData styleData = state.getGeckolibData(StyleDataTickets.TICKET_STYLE_DATA);
            if (styleData == null) return;
            if (!styleData.isEquipmentSlotVisible(slot)) return;
            renderArmorPiece(poseStack, nodeCollector, item, slot, packedLight, humanoidRenderState);
        }
    }
    //? } else {
    /*//? if neoforge {
    /^@Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void bounceStyles$skipArmorRendering(PoseStack poseStack, MultiBufferSource buffer, LivingEntity livingEntity, EquipmentSlot slot, int packedLight, HumanoidModel model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
    ^///? } else {
    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void bounceStyles$skipArmorRendering(PoseStack poseStack, MultiBufferSource buffer, T livingEntity, EquipmentSlot slot, int packedLight, A model, CallbackInfo ci) {
    //? }
        if(!(livingEntity instanceof Avatar) || !slot.isArmor()) return;

        StyleData styleData = StyleData.getEntityData((Avatar) livingEntity);
        if(!styleData.isEquipmentSlotVisible(slot)) ci.cancel();
    }
    *///? }

}
