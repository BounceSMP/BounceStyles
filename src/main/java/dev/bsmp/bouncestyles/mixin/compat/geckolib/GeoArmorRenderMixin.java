package dev.bsmp.bouncestyles.mixin.compat.geckolib;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
//? if >= 1.21.11 {
import dev.bsmp.bouncestyles.core.client.renderer.StyleDataTickets;
import software.bernie.geckolib.constant.DataTickets;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
//? } else {
/*import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import dev.bsmp.bouncestyles.api.data.StyleData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
*///? }

@Mixin(GeoArmorRenderer.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class GeoArmorRenderMixin {

    //? if >= 1.21.11 {
    @Inject(
            method = "submitRenderTasks",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitCustomGeometry(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;Lnet/minecraft/client/renderer/SubmitNodeCollector$CustomGeometryRenderer;)V"),
            cancellable = true
    )
    private void bounceStyles$hideSlot(RenderPassInfo<?> renderPassInfo, OrderedSubmitNodeCollector renderTasks, RenderType renderType, CallbackInfo ci) {
        if (renderPassInfo.renderState() instanceof AvatarRenderState) {
            var styleData = renderPassInfo.getGeckolibData(StyleDataTickets.TICKET_STYLE_DATA);
            var slot = renderPassInfo.getGeckolibData(DataTickets.EQUIPMENT_SLOT);
            if (styleData != null && slot != null && !styleData.isEquipmentSlotVisible(slot)) ci.cancel();
        }
    }
    //? } else {
    /*@Shadow protected Entity currentEntity;
    @Shadow protected EquipmentSlot currentSlot;

    @Inject(method = "renderToBuffer", at = @At("HEAD"), cancellable = true)
    private void bounceStyles$hideSlot(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int colour, CallbackInfo ci) {
        if (this.currentEntity instanceof Player player) {
            if (StyleData.hasStyleData(player)) {
                var styleData = StyleData.getEntityData(player);
                if (!styleData.isEquipmentSlotVisible(this.currentSlot)) ci.cancel();
            }
        }
    }
    *///? }

}
