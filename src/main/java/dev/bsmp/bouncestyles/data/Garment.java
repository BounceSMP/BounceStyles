package dev.bsmp.bouncestyles.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.List;

public class Garment implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public final ResourceLocation garmentId;
    public final ResourceLocation modelID;
    public final ResourceLocation textureID;
    @Nullable public final ResourceLocation animationID;
    @Nullable public final HashMap<String, String> animationMap;

    public List<String> hiddenParts;
    public int transitionTicks;

    public Garment(ResourceLocation garmentId, ResourceLocation modelID, ResourceLocation textureID, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap) {
        this.garmentId = garmentId;
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

    private PlayState predicate(AnimationEvent<Garment> animationEvent) {
        Player player = animationEvent.getExtraDataOfType(Player.class).get(0);
        AnimationController<?> controller = animationEvent.getController();

        if(animationMap != null && !animationMap.isEmpty()) {
            String anim;
            if(player.isFallFlying() && (anim = animationMap.get("flying")) != null)
                return applyAnimation(controller, anim);

            if(!player.isOnGround() && (anim = animationMap.get("in_air")) != null)
                return applyAnimation(controller, anim);

            else if(player.isShiftKeyDown() && (anim = animationMap.get("sneaking")) != null)
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

    private static boolean isEntityMoving(Player player) {
        double length = player.getDeltaMovement().length();
        return length > 0.1 || length < -0.1;
//        return !(player.animationSpeedOld > -0.15F && player.animationSpeedOld < 0.15F);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Garment))
            return false;
        return ((Garment)obj).garmentId.equals(this.garmentId);
    }

    @Override
    public int hashCode() {
        return this.garmentId.hashCode();
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
