package dev.bsmp.bouncestyles.core.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class Style implements GeoAnimatable {
    public static final DataTicket<Player> PLAYER = new DataTicket<>("player_entity", Player.class);

    public static final ResourceLocation MISSING_MODEL_ID = BounceStyles.resourceLocation("geo/missing_model.geo.json");
    public static final ResourceLocation MISSING_TEXTURE_ID = BounceStyles.resourceLocation("textures/missing_model.png");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final ResourceLocation styleId;
    private final ResourceLocation modelId;
    private final ResourceLocation textureId;
    private final ResourceLocation animationId;
    private final Map<String, String> animationMap;
    private final int transitionTicks;
    private final List<String> hiddenParts;
    private final List<BounceStylesRegistries.Category> categories;

    public Style(ResourceLocation styleId, ResourceLocation modelId, ResourceLocation textureId, ResourceLocation animationId, Map<String, String> animationMap) {
        this(styleId, modelId, textureId, animationId, animationMap, 0, List.of(), List.of());
    }

    public Style(ResourceLocation styleId, ResourceLocation modelId, ResourceLocation textureId, ResourceLocation animationId, Map<String, String> animationMap, int transitionTicks, List<String> hiddenParts, List<BounceStylesRegistries.Category> categories) {
        this.styleId = styleId;
        this.modelId = modelId;
        this.textureId = textureId;
        this.animationId = animationId;
        this.animationMap = animationMap != null ? new HashMap<>(animationMap) : null;
        this.transitionTicks = transitionTicks;
        this.hiddenParts = hiddenParts;
        this.categories = categories;
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

    public int getTransitionTicks() {
        return transitionTicks;
    }

    public ResourceLocation getTextureId() {
        return textureId;
    }

    public ResourceLocation getStyleId() {
        return styleId;
    }

    public ResourceLocation getModelId() {
        return modelId;
    }

    public List<String> getHiddenParts() {
        return hiddenParts;
    }

    public List<BounceStylesRegistries.Category> getCategories() {
        return categories;
    }

    public Optional<ResourceLocation> getAnimationId() {
        return Optional.ofNullable(animationId);
    }

    @Override
    public String toString() {
        return String.format(
                "[styleId=%s, modelId=%s, textureId=%s, animationId=%s, animationMap=%s]",
                styleId, modelId, textureId, animationId, animationMap
        );
    }

    private static Style decode(String styleName, Optional<ResourceLocation> modelId, Optional<ResourceLocation> textureId, Optional<ResourceLocation> animationId, Optional<Map<String, String>> animationMap, Optional<Integer> transitionTicks, Optional<List<String>> hiddenParts, List<BounceStylesRegistries.Category> categories) {
        var styleId = styleName.contains(":") ? new ResourceLocation(styleName) : BounceStyles.resourceLocation(styleName);
        return new Style(
                styleId,
                parseId(styleId, modelId, "geo", ".geo.json"),
                parseId(styleId, textureId, "textures", ".png"),
                parseId(styleId, animationId, "animations", ".animation.json"),
                animationMap.orElse(null),
                transitionTicks.orElse(0),
                hiddenParts.orElse(List.of()),
                categories
        );
    }

    private static ResourceLocation parseId(ResourceLocation styleName, Optional<ResourceLocation> resourceId, String directory, String suffix) {
        var id = resourceId.orElse(styleName);
        var path = id.getPath().endsWith(suffix) ? id.getPath() : id.getPath() + suffix;
        return new ResourceLocation(id.getNamespace(), directory + "/" + path);
    }

    private static final Codec<ResourceLocation> ID_CODEC = Codec.STRING.xmap(s -> {
        if (s.contains(":")) return ResourceLocation.tryParse(s);
        return BounceStyles.resourceLocation(s);
    }, ResourceLocation::toString);

    public static final Codec<Style> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(style -> style.getStyleId().toString()),
            ID_CODEC.optionalFieldOf("model_id").forGetter(style -> Optional.of(style.getModelId())),
            ID_CODEC.optionalFieldOf("texture_id").forGetter(style -> Optional.of(style.getTextureId())),
            ID_CODEC.optionalFieldOf("animation_id").forGetter(Style::getAnimationId),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("animations").forGetter(style -> Optional.ofNullable(style.animationMap)),
            Codec.INT.optionalFieldOf("transition_ticks").forGetter(style -> Optional.of(style.getTransitionTicks())),
            Codec.STRING.listOf().optionalFieldOf("hidden_parts").forGetter(style -> Optional.of(style.getHiddenParts())),
            BounceStylesRegistries.Category.CODEC.listOf().fieldOf("slots").forGetter(Style::getCategories)
    ).apply(instance, Style::decode));
}
