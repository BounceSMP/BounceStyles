package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.data.StyleEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerMixin implements StyleEntity {
    @Unique private StyleData bounceStyles$styleData;

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveStyleData(CompoundTag compound, CallbackInfo ci) {
        if(this.bounceStyles$styleData != null)
            StyleData.toNBT(this.bounceStyles$styleData).ifPresent(tag -> compound.put("bounceStyleData", tag));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readStyleData(CompoundTag compound, CallbackInfo ci) {
        if(compound.contains("bounceStyleData"))
            StyleData.fromNBT(compound.getCompound("bounceStyleData")).ifPresent(this::setStyleData);
    }

    @Override
    public void setStyleData(StyleData styleData) {
        if(((Object) this) instanceof Player)
            this.bounceStyles$styleData = styleData;
    }

    @Override
    public StyleData getOrCreateStyleData() {
        if(this.bounceStyles$styleData == null)
            this.bounceStyles$styleData = new StyleData(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        return this.bounceStyles$styleData;
    }
}
