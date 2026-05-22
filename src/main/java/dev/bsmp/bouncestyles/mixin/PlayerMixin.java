package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.api.StyleEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements StyleEntity {
    @Unique private StyleData bounceStyles$styleData;

    //? if >= 1.21.11 {
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void bounceStyles$saveStyleData(ValueOutput output, CallbackInfo ci) {
        if (this.bounceStyles$styleData != null)
            output.store(StyleData.DATA_TAG, StyleData.CODEC, this.bounceStyles$styleData);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void bounceStyles$readStyleData(ValueInput input, CallbackInfo ci) {
        input.read(StyleData.DATA_TAG, StyleData.CODEC).ifPresent(this::bounceStyles$setStyleData);
    }
    //? } else {
//    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
//    private void bounceStyles$saveStyleData(CompoundTag compound, CallbackInfo ci) {
//        if(this.bounceStyles$styleData != null)
//            StyleData.toNBT(this.bounceStyles$styleData).ifPresent(tag -> compound.put(StyleData.DATA_TAG, tag));
//    }
//
//    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
//    private void bounceStyles$readStyleData(CompoundTag compound, CallbackInfo ci) {
//        if(compound.contains(StyleData.DATA_TAG))
//            StyleData.fromNBT(compound.getCompound(StyleData.DATA_TAG)).ifPresent(this::setStyleData);
//    }
    //? }

    @Override
    public void bounceStyles$setStyleData(StyleData styleData) {
        if(((Object) this) instanceof Player)
            this.bounceStyles$styleData = styleData;
    }

    @Override
    public StyleData bounceStyles$getOrCreateStyleData() {
        if(this.bounceStyles$styleData == null)
            this.bounceStyles$styleData = new StyleData();
        return this.bounceStyles$styleData;
    }
}
