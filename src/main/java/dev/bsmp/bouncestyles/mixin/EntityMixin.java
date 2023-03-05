package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.data.StyleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(PlayerEntity.class)
public abstract class EntityMixin implements StyleEntity {
    private StyleData styleData;

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void saveStyleData(NbtCompound compound, CallbackInfo ci) {
        if(this.styleData != null)
            compound.put("bounceStyleData", StyleData.toNBT(this.styleData));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readStyleData(NbtCompound compound, CallbackInfo ci) {
        if(compound.contains("bounceStyleData"))
            this.styleData = StyleData.fromNBT(compound.getCompound("bounceStyleData"));
    }

    @Override
    public void setStyleData(StyleData styleData) {
        if(((Object) this) instanceof PlayerEntity)
            this.styleData = styleData;
    }

    @Override
    public StyleData getStyleData() {
        if(this.styleData == null)
            this.styleData = new StyleData(null, null, null, null, new ArrayList<>());
        return this.styleData;
    }
}
