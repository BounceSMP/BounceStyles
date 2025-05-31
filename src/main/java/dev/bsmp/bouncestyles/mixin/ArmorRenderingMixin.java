package dev.bsmp.bouncestyles.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.core.data.StyleData;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class ArmorRenderingMixin<T extends LivingEntity, A extends HumanoidModel<T>> {
    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void bounceStyles$skipArmorRendering(PoseStack poseStack, MultiBufferSource buffer, T livingEntity, EquipmentSlot slot, int packedLight, A model, CallbackInfo ci) {
        if(!(livingEntity instanceof Player) || !slot.isArmor()) return;

        StyleData styleData = StyleData.getOrCreateStyleData((Player) livingEntity);
        if(!styleData.isArmorVisible(slot)) ci.cancel();
    }
}
