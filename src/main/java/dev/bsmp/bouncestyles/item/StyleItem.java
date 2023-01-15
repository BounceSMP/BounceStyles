package dev.bsmp.bouncestyles.item;

import java.util.HashMap;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import dev.bsmp.bouncestyles.BounceStyles;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class StyleItem extends ArmorItem implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public final ResourceLocation modelID;
    public final ResourceLocation textureID;
    @Nullable
    public final ResourceLocation animationID;
    @Nullable public final HashMap<String, String> animationMap;

    public boolean useBackupModel = false;
    public List<String> hiddenParts;
    public int transitionTicks;

    public StyleItem(CreativeModeTab group, ResourceLocation modelID, ResourceLocation textureID, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap, EquipmentSlot slot) {
        super(BounceStyles.STYLE_MATERIAL, slot, new Properties().tab(group));
        this.modelID = modelID;
        this.textureID = textureID;
        this.animationID = animationID;
        this.animationMap = animationMap;
    }

    public static class HeadStyleItem extends StyleItem {
        public HeadStyleItem(ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap) {
            super(BounceStyles.HEAD_GROUP, model, texture, animationID, animationMap, EquipmentSlot.HEAD);
        }

        @Override
        public ResourceLocation getIconId() {
            return new ResourceLocation(BounceStyles.modId, "textures/item/bounce_head.png");
        }
    }
    public static class BodyStyleItem extends StyleItem {
        public BodyStyleItem(ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap) {
            super(BounceStyles.BODY_GROUP, model, texture, animationID,  animationMap, EquipmentSlot.CHEST);
        }

        @Override
        public ResourceLocation getIconId() {
            return new ResourceLocation(BounceStyles.modId, "textures/item/bounce_body.png");
        }
    }
    public static class LegsStyleItem extends StyleItem {
        public LegsStyleItem(ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap) {
            super(BounceStyles.LEGS_GROUP, model, texture, animationID,  animationMap, EquipmentSlot.LEGS);
        }

        @Override
        public ResourceLocation getIconId() {
            return new ResourceLocation(BounceStyles.modId, "textures/item/bounce_legs.png");
        }
    }
    public static class FeetStyleItem extends StyleItem {
        public FeetStyleItem(ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap) {
            super(BounceStyles.FEET_GROUP, model, texture, animationID,  animationMap, EquipmentSlot.FEET);
        }

        @Override
        public ResourceLocation getIconId() {
            return new ResourceLocation(BounceStyles.modId, "textures/item/bounce_feet.png");
        }
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        if(this.animationMap != null && !this.animationMap.isEmpty())
            animationData.addAnimationController(new AnimationController<>(this, "controller", this.transitionTicks, this::predicate));
    }

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        LivingEntity entity = event.getExtraDataOfType(LivingEntity.class).get(0);
        AnimationController<?> controller = event.getController();
        if(animationMap != null && !animationMap.isEmpty()) {
            String anim;
            if(entity.isFallFlying() && (anim = animationMap.get("flying")) != null)
                return applyAnimation(controller, anim);

            if(!entity.isOnGround() && (anim = animationMap.get("in_air")) != null)
                return applyAnimation(controller, anim);

            else if(entity.isShiftKeyDown() && (anim = animationMap.get("sneaking")) != null)
                return applyAnimation(controller, anim);

            else if (entity.isSwimming() && (anim = animationMap.get("swimming")) != null)
                return applyAnimation(controller, anim);

            else if(entity.isSprinting() && (anim = animationMap.get("sprinting")) != null)
                return applyAnimation(controller, anim);

            else if(isEntityMoving(entity) && (anim = animationMap.get("walking")) != null)
                return applyAnimation(controller, anim);

            else if(entity.isSleeping() && (anim = animationMap.get("sleeping")) != null)
                return applyAnimation(controller, anim);

            else if((anim = animationMap.get("idle")) != null)
                return applyAnimation(controller, anim);
        }
        return PlayState.STOP;
    }

    private static PlayState applyAnimation(AnimationController<?> controller, String anim) {
        if(isCurrentAnimation(controller, anim))
            return PlayState.CONTINUE;
        controller.setAnimation(new AnimationBuilder().addAnimation(anim));
        return PlayState.CONTINUE;
    }

    private static boolean isCurrentAnimation(AnimationController<?> controller, String animation) {
        return controller.getCurrentAnimation() != null && controller.getCurrentAnimation().animationName.equalsIgnoreCase(animation);
    }

    private static boolean isEntityMoving(LivingEntity entity) {
        return !(entity.animationSpeedOld > -0.15F && entity.animationSpeedOld < 0.15F);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public ResourceLocation getIconId() {
        return null;
    }

}
