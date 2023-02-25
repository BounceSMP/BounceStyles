package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.data.PlayerStyleData;
import dev.bsmp.bouncestyles.data.StyleEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(Player.class)
public class PlayerMixin implements StyleEntity {
    private PlayerStyleData styleData;

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveStyleData(CompoundTag compound, CallbackInfo ci) {
        if(this.styleData != null)
            compound.put("bounceStyleData", PlayerStyleData.toNBT(this.styleData));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readStyleData(CompoundTag compound, CallbackInfo ci) {
        if(compound.contains("bounceStyleData"))
            this.styleData = PlayerStyleData.fromNBT(compound.getCompound("bounceStyleData"));
    }

    @Override
    public void setStyleData(PlayerStyleData styleData) {
        this.styleData = styleData;
    }

    @Override
    public PlayerStyleData getStyleData() {
        if(this.styleData == null)
            this.styleData = new PlayerStyleData(null, null, null, null, new ArrayList<>());
        return this.styleData;
    }
}
