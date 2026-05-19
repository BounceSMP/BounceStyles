//? if fabric {
/*package dev.bsmp.bouncestyles.mixin.compat.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.core.data.StyleEntity;
import dev.emi.trinkets.TrinketFeatureRenderer;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrinketFeatureRenderer.class)
public abstract class TrinketRenderingMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method = "lambda$render$1", at = @At("HEAD"), cancellable = true)
    private void bounceStyles$shouldRender(PoseStack matrices, MultiBufferSource vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, SlotReference slotReference, ItemStack stack, CallbackInfo ci) {
        if (entity instanceof StyleEntity styleEntity) {
            var hiddenParts = styleEntity.getOrCreateStyleData().getHiddenParts();
            var slotType = slotReference.inventory().getSlotType();
            if (hiddenParts.contains("curios.*")
                || hiddenParts.contains("curios.%s.*".formatted(slotType.getGroup()))
                || hiddenParts.contains("curios.%s.%s".formatted(slotType.getGroup(), slotType.getName()))
            ) ci.cancel();
        }
    }
}
*///?}