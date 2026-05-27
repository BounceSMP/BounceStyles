package dev.bsmp.bouncestyles.mixin.common;

import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.api.StyleEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Avatar.class)
public abstract class AvatarMixin extends LivingEntity implements StyleEntity {
    @Unique private static final EntityDataAccessor<StyleData> bounceStyles$STYLE_DATA = SynchedEntityData.defineId(Avatar.class, STYLE_DATA_SERIALIZER);

    private AvatarMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void bounceStyles$defineStyleData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(bounceStyles$STYLE_DATA, new StyleData());
    }

    @Override
    public void bounceStyles$setStyleData(StyleData styleData) {
        this.getEntityData().set(bounceStyles$STYLE_DATA, styleData);
    }

    @Override
    public StyleData bounceStyles$getStyleData() {
        return this.getEntityData().get(bounceStyles$STYLE_DATA);
    }

    //? if >= 1.21.11 {
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store(StyleData.DATA_TAG, StyleData.CODEC, this.bounceStyles$getStyleData());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        input.read(StyleData.DATA_TAG, StyleData.CODEC).ifPresent(this::bounceStyles$setStyleData);
    }
    //? } else {
//    @Unique private StyleData bounceStyles$styleData;

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
}
