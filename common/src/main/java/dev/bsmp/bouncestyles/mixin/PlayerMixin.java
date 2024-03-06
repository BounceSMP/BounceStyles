package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.data.StyleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public abstract class PlayerMixin implements StyleEntity {
    private StyleData styleData;

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveStyleData(CompoundTag compound, CallbackInfo ci) {
        if(this.styleData != null)
            compound.put("bounceStyleData", StyleData.toNBT(this.styleData));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readStyleData(CompoundTag compound, CallbackInfo ci) {
        if(compound.contains("bounceStyleData"))
            this.styleData = StyleData.fromNBT(compound.getCompound("bounceStyleData"));
    }

    @Override
    public void setStyleData(StyleData styleData) {
        if(((Object) this) instanceof Player)
            this.styleData = styleData;
    }

    @Override
    public StyleData getOrCreateStyleData() {
        if(this.styleData == null)
            this.styleData = new StyleData(null, null, null, null, new ArrayList<>());
        return this.styleData;
    }
}
