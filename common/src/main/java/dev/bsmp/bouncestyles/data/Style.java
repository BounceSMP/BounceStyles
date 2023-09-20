package dev.bsmp.bouncestyles.data;

import dev.bsmp.bouncestyles.StyleRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Style implements GeoAnimatable {
    public static final DataTicket<PlayerEntity> PLAYER = new DataTicket<>("player_entity", PlayerEntity.class);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public final Identifier styleId;
    public final Identifier modelID;
    public final Identifier textureID;
    @Nullable public final Identifier animationID;

    @Nullable public final HashMap<String, String> animationMap;
    public int transitionTicks;
    public List<String> hiddenParts = new ArrayList<>();
    public List<StyleRegistry.Category> categories = new ArrayList<>();

    public Style(Identifier styleId, Identifier modelID, Identifier textureID, @Nullable Identifier animationID, @Nullable HashMap<String, String> animationMap) {
        this.styleId = styleId;
        this.modelID = modelID;
        this.textureID = textureID;
        this.animationID = animationID;
        this.animationMap = animationMap;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        if(animationMap != null && !animationMap.isEmpty()) {
            registrar.add(
                    new AnimationController<>(this, this.styleId.toString(), Math.max(transitionTicks, 1), this::predicate)
            );
        }
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }

    private PlayState predicate(AnimationState<Style> styleAnimationState) {
        PlayerEntity entity = styleAnimationState.getData(PLAYER);
        if (entity == null) return PlayState.STOP;

        AnimationController<?> controller = styleAnimationState.getController();
        if(animationMap != null && !animationMap.isEmpty()) {
            String anim;
            if(entity.isSleeping() && (anim = animationMap.get("sleeping")) != null)
                return applyAnimation(controller, anim);

            else if (entity.isSwimming() && (anim = animationMap.get("swimming")) != null)
                return applyAnimation(controller, anim);

            else if(entity.isFallFlying() && (anim = animationMap.get("flying")) != null)
                return applyAnimation(controller, anim);

            else if(!entity.isOnGround() && (anim = animationMap.get("in_air")) != null)
                return applyAnimation(controller, anim);

            else if(entity.isSneaking() && (anim = animationMap.get("sneaking")) != null)
                return applyAnimation(controller, anim);

            else if(entity.isSprinting() && (anim = animationMap.get("sprinting")) != null)
                return applyAnimation(controller, anim);

            else if(styleAnimationState.isMoving() && (anim = animationMap.get("walking")) != null)
                return applyAnimation(controller, anim);

            else if((anim = animationMap.get("idle")) != null)
                return applyAnimation(controller, anim);
        }

        return PlayState.STOP;
    }

    private static PlayState applyAnimation(AnimationController<?> controller, String anim) {
        if(isCurrentAnimation(controller, anim))
            return PlayState.CONTINUE;
        controller.setAnimation(RawAnimation.begin().thenLoop(anim));
        return PlayState.CONTINUE;
    }

    private static boolean isCurrentAnimation(AnimationController<?> controller, String animation) {
        return controller.getCurrentAnimation() != null && controller.getCurrentAnimation().animation().name().equalsIgnoreCase(animation);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public String toString() {
        return String.format(
                "[styleId=%s, modelId=%s, textureId=%s, animationId=%s, animationMap=%s]",
                styleId, modelID, textureID, animationID, animationMap
        );
    }
}
