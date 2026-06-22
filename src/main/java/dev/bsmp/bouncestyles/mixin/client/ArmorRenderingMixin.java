package dev.bsmp.bouncestyles.mixin.client;
//~ avatar

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 1.21.5 {
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
//? } else {

//? }

@Mixin(HumanoidArmorLayer.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
//? if >= 1.21.11 {
public class ArmorRenderingMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> {
//? } else
//public class ArmorRenderingMixin<T extends LivingEntity, A extends HumanoidModel<T>> {

    //? if >= 1.21.11 {
    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void bounceStyles$skipArmorRendering(PoseStack poseStack, SubmitNodeCollector nodeCollector, ItemStack item, EquipmentSlot slot, int packedLight, S renderState, CallbackInfo ci) {
        //ToDo fix armor hiding
//        var livingEntity = renderState.entityType
//        if(!(livingEntity instanceof Avatar) || !slot.isArmor()) return;
//
//        StyleData styleData = StyleData.getOrCreateStyleData((Avatar) livingEntity);
//        if(!styleData.isArmorVisible(slot)) ci.cancel();
    }
    //? } else {
    /*@Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void bounceStyles$skipArmorRendering(PoseStack poseStack, MultiBufferSource buffer, T livingEntity, EquipmentSlot slot, int packedLight, A model, CallbackInfo ci) {
        if(!(livingEntity instanceof Avatar) || !slot.isArmor()) return;

        StyleData styleData = StyleData.getEntityData((Avatar) livingEntity);
        if(!styleData.isEquipmentSlotVisible(slot)) ci.cancel();
    }
    *///? }

}
