package dev.bsmp.bouncestyles.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import dev.bsmp.bouncestyles.BounceStyles;
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

    public final Identifier modelID;
    public final Identifier textureID;
    @Nullable public final Identifier animationID;
    @Nullable public final HashMap<String, String> animationMap;
    public boolean useBackupModel = false;
    public List<String> hiddenParts;

    public StyleItem(ItemGroup group, Identifier modelID, Identifier textureID, @Nullable Identifier animationID, @Nullable HashMap<String, String> animationMap) {
        super(BounceStyles.STYLE_MATERIAL, EquipmentSlot.MAINHAND, new Settings().group(group));
        this.modelID = modelID;
        this.textureID = textureID;
        this.animationID = animationID;
        this.animationMap = animationMap;
    }

    public static class HeadStyleItem extends StyleItem {
        public HeadStyleItem(Identifier model, Identifier texture, @Nullable Identifier animationID, @Nullable HashMap<String, String> animationMap) {
            super(BounceStyles.HEAD_GROUP, model, texture, animationID, animationMap);
        }

        @Override
        public Identifier getIconId() {
            return new Identifier(BounceStyles.modId, "textures/item/bounce_head.png");
        }
    }
    public static class BodyStyleItem extends StyleItem {
        public BodyStyleItem(Identifier model, Identifier texture, @Nullable Identifier animationID, @Nullable HashMap<String, String> animationMap) {
            super(BounceStyles.BODY_GROUP, model, texture, animationID,  animationMap);
        }

        @Override
        public Identifier getIconId() {
            return new Identifier(BounceStyles.modId, "textures/item/bounce_body.png");
        }
    }
    public static class LegsStyleItem extends StyleItem {
        public LegsStyleItem(Identifier model, Identifier texture, @Nullable Identifier animationID, @Nullable HashMap<String, String> animationMap) {
            super(BounceStyles.LEGS_GROUP, model, texture, animationID,  animationMap);
        }

        @Override
        public Identifier getIconId() {
            return new Identifier(BounceStyles.modId, "textures/item/bounce_legs.png");
        }
    }
    public static class FeetStyleItem extends StyleItem {
        public FeetStyleItem(Identifier model, Identifier texture, @Nullable Identifier animationID, @Nullable HashMap<String, String> animationMap) {
            super(BounceStyles.FEET_GROUP, model, texture, animationID,  animationMap);
        }

        @Override
        public Identifier getIconId() {
            return new Identifier(BounceStyles.modId, "textures/item/bounce_feet.png");
        }
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        if(this.animationMap != null && !this.animationMap.isEmpty())
            animationData.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        LivingEntity entity = event.getExtraDataOfType(LivingEntity.class).get(0);
        AnimationController<?> controller = event.getController();
        if(animationMap != null && !animationMap.isEmpty()) {
            if(entity.isSneaking() && animationMap.containsKey("sneaking"))
                return applyAnimation(controller, animationMap.get("sneaking"));

            else if(entity.isSprinting() && animationMap.containsKey("sprinting"))
                return applyAnimation(controller, animationMap.get("sprinting"));

            else if(isEntityMoving(entity) && animationMap.containsKey("walking"))
                return applyAnimation(controller, animationMap.get("walking"));

            else if(animationMap.containsKey("idle"))
                return applyAnimation(controller, animationMap.get("idle"));
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
        return !(entity.lastLimbDistance > -0.15F && entity.lastLimbDistance < 0.15F);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public Identifier getIconId() {
        return null;
    }

}
