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
        return style.getTextureId();
    }

    @Override
    public ResourceLocation getAnimationResource(Style style) {
        return style.getAnimationId().orElse(null);
    }
}
