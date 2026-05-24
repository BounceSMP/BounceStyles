package dev.bsmp.bouncestyles.core.client.renderer;

import dev.bsmp.bouncestyles.core.data.Style;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
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

    @Override
    public @NonNull Identifier getModelResource(GeoRenderState renderState) {
        var style = renderState.getGeckolibData(StyleLayerRenderer.TICKET_STYLE);
        return style != null ? style.getStyleId().orElse(Style.MISSING_MODEL_ID) : Style.MISSING_MODEL_ID;
    }

    @Override
    public @NonNull Identifier getTextureResource(GeoRenderState renderState) {
        var style = renderState.getGeckolibData(StyleLayerRenderer.TICKET_STYLE);
        return style != null ? style.getTextureId().orElse(Style.MISSING_TEXTURE_ID) : Style.MISSING_TEXTURE_ID;
    }

    @Override
    public @NonNull Identifier getAnimationResource(Style style) {
        return style.getAnimationId().orElse(Identifier.withDefaultNamespace(""));
    }

    @Override
    public @NonNull BakedGeoModel getBakedModel(@NonNull Identifier location) {
        //? if >= 1.21.11 {
        if (!GeckoLibResources.getBakedModels().cache().containsKey(location)) location = Style.MISSING_MODEL_ID;
        //? } else
//        if (!GeckoLibCache.getBakedModels().containsKey(location)) location = Style.MISSING_MODEL_ID;
        return super.getBakedModel(location);
    }

//    @Override
//    public Identifier getModelResource(Style style) {
//        return style.getModelId();
//    }

//    @Override
//    public Identifier getTextureResource(Style style) {
//        if (!GeckoLibCache.getBakedModels().containsKey(style.getModelId())) return Style.MISSING_TEXTURE_ID;
//        return style.getTextureId();
//    }

    //? if <= 1.20.1 {
    /*@Override
    public software.bernie.geckolib.core.animation.Animation getAnimation(Style animatable, String name) {
        if (!GeckoLibCache.getBakedAnimations().containsKey(getAnimationResource(animatable)))
            return null;
        return super.getAnimation(animatable, name);
    }
    *///?}
}
