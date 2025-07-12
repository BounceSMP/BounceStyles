package dev.bsmp.bouncestyles.api.style;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.bsmp.bouncestyles.core.BounceStyles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.util.GeckoLibUtil;
//? if <= 1.20.1 {
/*import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.core.object.PlayState;
*///?} else if >= 1.21.1 {
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.dataticket.DataTicket;
//?}

import java.util.*;

public class Style implements GeoAnimatable {
    public static final ResourceLocation MISSING_MODEL_ID = BounceStyles.resourceLocation("geo/missing_model.geo.json");
    public static final ResourceLocation MISSING_TEXTURE_ID = BounceStyles.resourceLocation("textures/missing_model.png");

    public static final DataTicket<Player> PLAYER = new DataTicket<>("player_entity", Player.class);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this, true);

    private final ResourceLocation styleId;
    private final ResourceLocation modelId;
    private final ResourceLocation textureId;
    private final @Nullable List<ResourceLocation> textureVariants;
    private final @Nullable ResourceLocation animationId;
    private final @Nullable Map<String, RawAnimation> animationMap;

    private final int transitionTicks;
    private final List<Category> categories;
    private final @Nullable List<String> hiddenParts;
    private final @Nullable List<String> credits;

    public Style(ResourceLocation styleId, ResourceLocation modelId, ResourceLocation textureId, @Nullable ResourceLocation animationId, @Nullable Map<String, String> animationMap, List<Category> categories) {
        this(styleId, modelId, textureId, null, animationId, animationMap, 1, null, categories, null);
    }

    public Style(ResourceLocation styleId, ResourceLocation modelId, ResourceLocation textureId, @Nullable List<ResourceLocation> textureVariants, @Nullable ResourceLocation animationId, @Nullable Map<String, String> animationMap, int transitionTicks, List<Category> categories) {
        this(styleId, modelId, textureId, textureVariants, animationId, animationMap, transitionTicks, null, categories, null);
    }

    public Style(ResourceLocation styleId, ResourceLocation modelId, ResourceLocation textureId, @Nullable List<ResourceLocation> textureVariants, @Nullable ResourceLocation animationId, @Nullable Map<String, String> animationMap, int transitionTicks, @Nullable List<String> hiddenParts, List<Category> categories, @Nullable List<String> credits) {
        this.styleId = styleId;
        this.modelId = modelId;
        this.textureId = textureId;
        this.textureVariants = textureVariants;
        this.animationId = animationId;
        this.animationMap = animationMap != null ? buildAnimationMap(animationMap) : null;
        this.transitionTicks = transitionTicks;
        this.categories = categories;
        this.hiddenParts = hiddenParts;
        this.credits = credits;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        if(animationMap != null && !animationMap.isEmpty()) {
            registrar.add(
                    new AnimationController<>(this, this.styleId.toString(), Math.max(transitionTicks, 0), this::predicate)
            );
        }
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }

    private PlayState predicate(AnimationState<Style> styleAnimationState) {
        Player entity = styleAnimationState.getData(PLAYER);
        if (entity == null) return PlayState.CONTINUE;

        AnimationController<?> controller = styleAnimationState.getController();
        if(animationMap != null && !animationMap.isEmpty()) {
            RawAnimation anim;
            //ToDo Consider supporting EmoteCraft emote-specific animations, if specified as something like "emote.emote_name"
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

    private static PlayState applyAnimation(AnimationController<?> controller, RawAnimation anim) {
        controller.setAnimation(anim);
        return PlayState.CONTINUE;
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

    public Optional<List<ResourceLocation>> getTextureVariants() {
        return Optional.ofNullable(this.textureVariants);
    }

    public ResourceLocation getStyleId() {
        return styleId;
    }

    public ResourceLocation getModelId() {
        return modelId;
    }

    public Optional<List<String>> getHiddenParts() {
        return Optional.ofNullable(hiddenParts);
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Optional<ResourceLocation> getAnimationId() {
        return Optional.ofNullable(animationId);
    }

    public Optional<Map<String, RawAnimation>> getAnimationMap() {
        return Optional.ofNullable(this.animationMap);
    }

    public Optional<Map<String, String>> getAnimationStringMap() {
        if (this.animationMap == null) return Optional.empty();

        Map<String, String> stringMap = new HashMap<>();
        this.animationMap.forEach((s, rawAnimation) -> {
            //ToDo If animation sequences are implemented, join them together with ';' or something, matching the input string
            stringMap.put(s, rawAnimation.getAnimationStages().getLast().animationName());
        });
        return Optional.of(stringMap);
    }

    public Optional<List<String>> getCredits() {
        return Optional.ofNullable(this.credits);
    }

    @Override
    public String toString() {
        return String.format(
                "[style=%s, modelId=%s, textureId=%s, animationId=%s, animationMap=%s]",
                styleId, modelId, textureId, animationId, animationMap
        );
    }

    //ToDo Consider adding a way to define a sequence of animations to play, separated by ';' or something
    private static Map<String, RawAnimation> buildAnimationMap(Map<String, String> map) {
        Map<String, RawAnimation> animMap = new HashMap<>();
        map.forEach((state, anim) -> {
            animMap.put(state, RawAnimation.begin().thenLoop(anim));
        });
        return animMap;
    }

    private static Style decode(String styleName, Optional<ResourceLocation> modelId, Optional<ResourceLocation> textureId, Optional<List<ResourceLocation>> textureVariants, Optional<ResourceLocation> animationId, Optional<Map<String, String>> animationMap, Optional<Integer> transitionTicks, Optional<List<String>> hiddenParts, List<Category> categories, Optional<List<String>> credits) {
        var styleId = BounceStyles.resourceLocation(styleName);
        if (textureVariants.isPresent()) {
            if (!textureVariants.get().isEmpty()) {
                List<ResourceLocation> list = new ArrayList<>();
                for (ResourceLocation id : textureVariants.get()) {
                    list.add(parseId(styleId, Optional.of(id), "textures", ".png"));
                }
                textureVariants = Optional.of(list);
            }
            else
                textureVariants = Optional.empty();
        }
        return new Style(
                styleId,
                parseId(styleId, modelId, "geo", ".geo.json"),
                parseId(styleId, textureId, "textures", ".png"),
                textureVariants.orElse(null),
                parseId(styleId, animationId, "animations", ".animation.json"),
                animationMap.orElse(null),
                transitionTicks.orElse(0),
                hiddenParts.orElse(null),
                categories,
                credits.orElse(null)
        );
    }

    private static ResourceLocation parseId(ResourceLocation styleName, Optional<ResourceLocation> resourceId, String directory, String suffix) {
        var id = resourceId.orElse(styleName);
        var path = id.getPath().endsWith(suffix) ? id.getPath() : id.getPath() + suffix;
        if (!path.startsWith(directory)) path = directory + "/" + path;
        //? if <= 1.20.1 {
        /*return new ResourceLocation(id.getNamespace(), path);
        *///?} else if >= 1.21.1 {
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path);
        //?}
    }

    private static final Codec<ResourceLocation> ID_CODEC = Codec.STRING.xmap(s -> {
        if (s.contains(":")) return ResourceLocation.tryParse(s);
        return BounceStyles.resourceLocation(s);
    }, ResourceLocation::toString);

    public static final Codec<Style> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(style -> style.getStyleId().toString()),
            ID_CODEC.optionalFieldOf("model_id").forGetter(style -> Optional.of(style.getModelId())),
            ID_CODEC.optionalFieldOf("texture_id").forGetter(style -> Optional.of(style.getTextureId())),
            ID_CODEC.listOf().optionalFieldOf("texture_variants").forGetter(Style::getTextureVariants),
            ID_CODEC.optionalFieldOf("animation_id").forGetter(Style::getAnimationId),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("animations").forGetter(Style::getAnimationStringMap),
            Codec.INT.optionalFieldOf("transition_ticks").forGetter(style -> Optional.of(style.getTransitionTicks())),
            Codec.STRING.listOf().optionalFieldOf("hidden_parts").forGetter(Style::getHiddenParts),
            Category.CODEC.listOf().fieldOf("slots").forGetter(Style::getCategories),
            Codec.STRING.listOf().optionalFieldOf("credits").forGetter(Style::getCredits)
    ).apply(instance, Style::decode));

}
