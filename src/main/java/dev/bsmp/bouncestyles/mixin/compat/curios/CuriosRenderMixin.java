//? neoforge {
/*package dev.bsmp.bouncestyles.mixin.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.SlotContext;
import net.minecraft.client.renderer.MultiBufferSource;

//? if >= 1.21.11 {
import top.theillusivec4.curios.api.client.ICurioRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.item.ItemStack;
import dev.bsmp.bouncestyles.core.client.renderer.StyleDataTickets;
//? } else {
/^import com.llamalad7.mixinextras.sugar.Local;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.client.render.CuriosLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import dev.bsmp.bouncestyles.api.data.StyleData;
^///? }

//~ if >= 1.21.11 'CuriosLayer' -> 'ICurioRenderer'
@Mixin(ICurioRenderer.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
//~ if >= 1.21.11 'abstract class' -> 'interface'
public interface CuriosRenderMixin {

    //? if >= 1.21.11 {
    @Inject(
            method = "render(Lnet/minecraft/world/item/ItemStack;Ltop/theillusivec4/curios/api/SlotContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lnet/minecraft/client/renderer/entity/RenderLayerParent;Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;FF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void bounceStyles$hideSlot(ItemStack stack, SlotContext slotContext, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int packedLight, LivingEntityRenderState renderState, RenderLayerParent renderLayerParent, EntityRendererProvider.Context context, float yRotation, float xRotation, CallbackInfo ci) {
        if (renderState instanceof AvatarRenderState) {
            var styleData = ((GeoRenderState) renderState).getGeckolibData(StyleDataTickets.TICKET_STYLE_DATA);
            if (styleData != null && styleData.getHiddenParts().contains(slotString(slotContext.identifier())))
                ci.cancel();
        }
    }
    //? } else {
    /^@Inject(method = "lambda$render$1", at = @At(value = "INVOKE", target = "Ltop/theillusivec4/curios/api/client/CuriosRendererRegistry;getRenderer(Lnet/minecraft/world/item/Item;)Ljava/util/Optional;"), cancellable = true)
    private void bounceStyles$hideSlot(LivingEntity livingEntity, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, String id, ICurioStacksHandler stacksHandler, CallbackInfo ci, @Local SlotContext slotContext) {
        if (livingEntity instanceof Player player) {
            if (StyleData.hasStyleData(player)) {
                var styleData = StyleData.getEntityData(player);
                if (styleData.getHiddenParts().contains(slotString(slotContext.identifier().toString())))
                    ci.cancel();
            }
        }
    }
    ^///? }

    private static String slotString(String identifier) {
        return "curios."+identifier.replaceAll(":", ".");
    }

}
*///? }