package dev.bsmp.bouncestyles.data;

import dev.bsmp.bouncestyles.StyleRegistry;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class Style implements GeoAnimatable {
    public static final DataTicket<Player> PLAYER = new DataTicket<>("player_entity", Player.class);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public final ResourceLocation styleId;
    public final ResourceLocation modelID;
    public final ResourceLocation textureID;
    @Nullable public final ResourceLocation animationID;

    @Nullable public final HashMap<String, String> animationMap;
    public int transitionTicks;
    public List<String> hiddenParts = new ArrayList<>();
    public List<StyleRegistry.Category> categories = new ArrayList<>();

    public Style(ResourceLocation styleId, ResourceLocation modelID, ResourceLocation textureID, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap) {
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
        Player entity = styleAnimationState.getData(PLAYER);
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

            else if(!entity.onGround() && (anim = animationMap.get("in_air")) != null)
                return applyAnimation(controller, anim);

            else if(entity.isShiftKeyDown() && (anim = animationMap.get("sneaking")) != null)
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
