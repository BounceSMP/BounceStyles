package dev.bsmp.bouncestyles.core.client.renderer;

import dev.bsmp.bouncestyles.core.data.Style;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.cache.GeckoLibResources;
import software.bernie.geckolib.model.GeoModel;
//? if >= 1.21.11 {
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
//? } else {
//import software.bernie.geckolib.cache.GeckoLibCache;
//import software.bernie.geckolib.cache.object.BakedGeoModel;
//? }

public class StyleGeoModel extends GeoModel<Style> {
//    @Override
//    public Identifier getModelResource(Style style) {
//        return style.getModelId();
//    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return null;
    }

    @Override
    public BakedGeoModel getBakedModel(Identifier location) {
        //? if >= 1.21.11 {
        if (!GeckoLibResources.getBakedModels().cache().containsKey(location)) location = Style.MISSING_MODEL_ID;
        //? } else
//        if (!GeckoLibCache.getBakedModels().containsKey(location)) location = Style.MISSING_MODEL_ID;
        return super.getBakedModel(location);
    }

//    @Override
//    public Identifier getTextureResource(Style style) {
//        if (!GeckoLibCache.getBakedModels().containsKey(style.getModelId())) return Style.MISSING_TEXTURE_ID;
//        return style.getTextureId();
//    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return null;
    }

    @Override
    public Identifier getAnimationResource(Style style) {
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
