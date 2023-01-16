package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.client.renderer.StyleArmorRenderer;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.item.ItemStack;
import dev.bsmp.bouncestyles.item.StyleItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;
import java.util.Map;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "setModelProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isCrouching()Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void checkStyleVisibility(AbstractClientPlayer player, CallbackInfo ci, PlayerModel<?> playerEntityModel) {
        List<SlotResult> results = CuriosApi.getCuriosHelper().findCurios(player,
                StyleItem.HeadStyleItem.curioSlot,
                StyleItem.BodyStyleItem.curioSlot,
                StyleItem.LegsStyleItem.curioSlot,
                StyleItem.FeetStyleItem.curioSlot
        );

        for(SlotResult slot : results) {
            if(slot.getStack().getItem() instanceof StyleItem) {
                StyleItem item = (StyleItem) slot.getStack().getItem();
                StyleArmorRenderer.hideParts(playerEntityModel, item);
            }
        }

        for(ItemStack itemStack : player.getArmorSlots())
            if(itemStack.getItem() instanceof StyleItem)
                StyleArmorRenderer.hideParts(playerEntityModel, (StyleItem) itemStack.getItem());
    }

}
