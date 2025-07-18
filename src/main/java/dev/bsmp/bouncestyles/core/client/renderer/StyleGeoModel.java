package dev.bsmp.bouncestyles.core.client.renderer;

import dev.bsmp.bouncestyles.api.style.Style;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class StyleGeoModel extends GeoModel<Style> {
    @Override
    public ResourceLocation getModelResource(Style style) {
        return style.getModelId();
    }

    @Override
    public BakedGeoModel getBakedModel(ResourceLocation location) {
        if (!GeckoLibCache.getBakedModels().containsKey(location)) location = Style.MISSING_MODEL_ID;
        return super.getBakedModel(location);
    }

    @Override
    public ResourceLocation getTextureResource(Style style) {
        if (!GeckoLibCache.getBakedModels().containsKey(style.getModelId())) return Style.MISSING_TEXTURE_ID;
        return style.getTextureId();
    }

    @Override
    public ResourceLocation getAnimationResource(Style style) {
        return style.getAnimationId().orElse(null);
    }

    //? if <= 1.20.1 {
    /*@Override
    public software.bernie.geckolib.core.animation.Animation getAnimation(Style animatable, String name) {
        if (!GeckoLibCache.getBakedAnimations().containsKey(getAnimationResource(animatable)))
            return null;
        return super.getAnimation(animatable, name);
    }
    *///?}
}
