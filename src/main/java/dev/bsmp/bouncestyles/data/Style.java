package dev.bsmp.bouncestyles.data;

import dev.bsmp.bouncestyles.StyleLoader;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class Style implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public final Identifier styleId;
    public final Identifier modelID;
    public final Identifier textureID;
    @Nullable public final Identifier animationID;

    @Nullable public final HashMap<String, String> animationMap;
    public int transitionTicks;
    public List<String> hiddenParts = new ArrayList<>();
    public List<StyleLoader.Category> categories = new ArrayList<>();

    public Style(Identifier styleId, Identifier modelID, Identifier textureID, @Nullable Identifier animationID, @Nullable HashMap<String, String> animationMap) {
        this.styleId = styleId;
        this.modelID = modelID;
        this.textureID = textureID;
        this.animationID = animationID;
        this.animationMap = animationMap;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        if(animationMap != null && !animationMap.isEmpty()) {
            animationData.addAnimationController(
                    new AnimationController<>(this, "base", transitionTicks, this::predicate)
            );
        }
    }

    private PlayState predicate(AnimationEvent<Style> animationEvent) {
        PlayerEntity player = animationEvent.getExtraDataOfType(PlayerEntity.class).get(0);
        AnimationController<?> controller = animationEvent.getController();

        if(animationMap != null && !animationMap.isEmpty()) {
            String anim;
            if(player.isFallFlying() && (anim = animationMap.get("flying")) != null)
                return applyAnimation(controller, anim);

            if(!player.isOnGround() && (anim = animationMap.get("in_air")) != null)
                return applyAnimation(controller, anim);

            else if(player.isSneaking() && (anim = animationMap.get("sneaking")) != null)
                return applyAnimation(controller, anim);

            else if (player.isSwimming() && (anim = animationMap.get("swimming")) != null)
                return applyAnimation(controller, anim);

            else if(player.isSprinting() && (anim = animationMap.get("sprinting")) != null)
                return applyAnimation(controller, anim);

            else if(isEntityMoving(player) && (anim = animationMap.get("walking")) != null)
                return applyAnimation(controller, anim);

            else if(player.isSleeping() && (anim = animationMap.get("sleeping")) != null)
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

    private static boolean isEntityMoving(PlayerEntity player) {
        double length = player.getVelocity().length();
        return length > 0.1 || length < -0.1;
//        return !(player.animationSpeedOld > -0.15F && player.animationSpeedOld < 0.15F);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Style))
            return false;
        return ((Style)obj).styleId.equals(this.styleId);
    }

    @Override
    public int hashCode() {
        return this.styleId.hashCode();
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}