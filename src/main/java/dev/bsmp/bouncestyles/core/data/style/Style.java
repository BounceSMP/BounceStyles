package dev.bsmp.bouncestyles.core.data.style;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.data.Category;
import dev.bsmp.bouncestyles.core.data.animation.AnimationHandler;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.util.GeckoLibUtil;
//? if >= 1.21.11 {
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.dataticket.DataTicket;
//? } elif >= 1.21.1 {
/*import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.dataticket.DataTicket;
*///? } else {
/*import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.core.object.PlayState;
*///? }

import java.util.*;

public class Style implements GeoAnimatable {
    //? if >= 1.21.11 {
    public static final Identifier MISSING_MODEL_ID = BounceStyles.id("missing_model");
    //? } else
//    public static final Identifier MISSING_MODEL_ID = BounceStyles.id("geo/missing_model.geo.json");

    public static final Identifier MISSING_TEXTURE_ID = BounceStyles.id("textures/missing_model.png");

    //? if >= 1.21.11 {
    public static final DataTicket<Player> PLAYER = DataTicket.create("player_entity", Player.class);
    //? } else {
    /*public static final DataTicket<Player> PLAYER = new DataTicket<>("player_entity", Player.class);
    *///? }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this, false);

    private final Identifier styleId;
    private final Identifier modelId;
    private final Identifier textureId;
    private final @Nullable List<Identifier> textureVariants;
    private final @Nullable Identifier animationId;
    private final @Nullable Map<String, RawAnimation> animationMap;

    private final int transitionTicks;
    private final List<Category> categories;
    private final @Nullable List<String> hiddenParts;
    private final @Nullable List<String> credits;

    public Style(Identifier styleId, Identifier modelId, Identifier textureId, @Nullable Identifier animationId, @Nullable Map<String, String> animationMap, List<Category> categories) {
        this(styleId, modelId, textureId, null, animationId, animationMap, 1, null, categories, null);
    }

    public Style(Identifier styleId, Identifier modelId, Identifier textureId, @Nullable List<Identifier> textureVariants, @Nullable Identifier animationId, @Nullable Map<String, String> animationMap, int transitionTicks, List<Category> categories) {
        this(styleId, modelId, textureId, textureVariants, animationId, animationMap, transitionTicks, null, categories, null);
    }

    public Style(Identifier styleId, Identifier modelId, Identifier textureId, @Nullable List<Identifier> textureVariants, @Nullable Identifier animationId, @Nullable Map<String, String> animationMap, int transitionTicks, @Nullable List<String> hiddenParts, List<Category> categories, @Nullable List<String> credits) {
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
            registrar.add(new AnimationController<>(
                    this.styleId.toString(),
                    Math.max(transitionTicks, 0),
                    AnimationHandler::handleAnimState
            ));
        }
    }

    public boolean hasVariants() {
        return this.textureVariants != null && !this.textureVariants.isEmpty();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public int getTransitionTicks() {
        return transitionTicks;
    }

    public Identifier getTextureId() {
        return textureId;
    }

    public Identifier getTextureId(int variant) {
        if (!this.hasVariants() || variant < 0 || variant >= this.textureVariants.size()) return this.getTextureId();
        return this.textureVariants.get(variant);
    }

    public Optional<List<Identifier>> getTextureVariants() {
        return Optional.ofNullable(this.textureVariants);
    }

    public Identifier getStyleId() {
        return styleId;
    }

    public Identifier getModelId() {
        return modelId;
    }

    public Optional<List<String>> getHiddenParts() {
        return Optional.ofNullable(hiddenParts);
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Optional<Identifier> getAnimationId() {
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
            stringMap.put(s, rawAnimation.getAnimationStages().get(rawAnimation.getAnimationStages().size() - 1).animationName());
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

    //? if <= 1.21.1 {
    /*@Override
    public double getTick(Object o) {
        return 0;
    }
    *///? }

    //ToDo Consider adding a way to define a sequence of animations to play, separated by ';' or something
    private static Map<String, RawAnimation> buildAnimationMap(Map<String, String> map) {
        Map<String, RawAnimation> animMap = new HashMap<>();
        map.forEach((state, anim) -> {
            animMap.put(state, RawAnimation.begin().thenLoop(anim));
        });
        return animMap;
    }

    private static Style decode(String styleName, Optional<Identifier> modelId, Optional<Identifier> textureId, Optional<List<Identifier>> textureVariants, Optional<Identifier> animationId, Optional<Map<String, String>> animationMap, Integer transitionTicks, Optional<List<String>> hiddenParts, List<Category> categories, Optional<List<String>> credits) {
        var styleId = BounceStyles.id(styleName);
        if (textureVariants.isPresent()) {
            if (!textureVariants.get().isEmpty()) {
                List<Identifier> list = new ArrayList<>();
                for (Identifier id : textureVariants.get()) {
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
                transitionTicks,
                hiddenParts.orElse(null),
                categories,
                credits.orElse(null)
        );
    }

    private static Identifier parseId(Identifier styleName, Optional<Identifier> resourceId, String directory, String suffix) {
        var id = resourceId.orElse(styleName);

        //? if >= 1.21.11 {
        if (directory.equalsIgnoreCase("textures") && !id.getPath().startsWith(directory))
            id = id.withPath(directory+"/"+id.getPath());
        return id.withPath(id.getPath().replace(".geo.json", "").replace(".animation.json", ""));
        //? } else {
//        var path = id.getPath().endsWith(suffix) ? id.getPath() : id.getPath() + suffix;
//        if (!path.startsWith(directory)) path = directory + "/" + path;
        //? if >= 1.21.1 {
//        return Identifier.fromNamespaceAndPath(id.getNamespace(), path);
        //? } else {
        /*return new Identifier(id.getNamespace(), path);
        *///? }
        //? }
    }

    private static final Codec<Identifier> ID_CODEC = Codec.STRING.xmap(s -> {
        if (s.contains(":")) return Identifier.tryParse(s);
        return BounceStyles.id(s);
    }, Identifier::toString);

    public static final Codec<Style> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(style -> style.getStyleId().toString()),
            ID_CODEC.optionalFieldOf("model_id").forGetter(style -> Optional.of(style.getModelId())),
            ID_CODEC.optionalFieldOf("texture_id").forGetter(style -> Optional.of(style.getTextureId())),
            ID_CODEC.listOf().optionalFieldOf("texture_variants").forGetter(Style::getTextureVariants),
            ID_CODEC.optionalFieldOf("animation_id").forGetter(Style::getAnimationId),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("animations").forGetter(Style::getAnimationStringMap),
            Codec.INT.optionalFieldOf("transition_ticks", 0).forGetter(Style::getTransitionTicks),
            Codec.STRING.listOf().optionalFieldOf("hidden_parts").forGetter(Style::getHiddenParts),
            Category.CODEC.listOf().fieldOf("slots").forGetter(Style::getCategories),
            Codec.STRING.listOf().optionalFieldOf("credits").forGetter(Style::getCredits)
    ).apply(instance, Style::decode));

}
