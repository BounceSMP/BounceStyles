package dev.bsmp.bouncestyles.core.client.renderer;

import dev.bsmp.bouncestyles.api.style.Style;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.cache.GeckoLibResources;
import software.bernie.geckolib.model.GeoModel;
//? if >= 1.21.5 {
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.cache.animation.Animation;
//? } else {
/*import software.bernie.geckolib.animation.Animation;
*///? }

import static dev.bsmp.bouncestyles.core.client.renderer.StyleDataTickets.TICKET_EQUIPPED;

public class StyleGeoModel extends GeoModel<Style> {
    //? if < 1.21.5
    //@Override
    public @NonNull Identifier getModelResource(Style style) {
        return style != null ? style.getModelId() : Style.MISSING_MODEL_ID;
    }

    //? if < 1.21.5 {
    /*@Override
    public @NonNull Identifier getTextureResource(Style style) {
        return style != null ? style.getTextureId() : Style.MISSING_MODEL_TEXTURE_ID;
    }
    *///? }

    @Override
    public @NonNull Identifier getAnimationResource(Style style) {
        if (style != null && style.getAnimationId().isPresent())
            return style.getAnimationId().get();
        return Identifier.withDefaultNamespace("");
    }

    //? if >= 1.21.5 {
    @Override
    public @NonNull Identifier getModelResource(GeoRenderState renderState) {
        var style = renderState.getGeckolibData(TICKET_EQUIPPED);
        return getModelResource(style.getStyle().orElse(null));
    }

    @Override
    public @NonNull Identifier getTextureResource(GeoRenderState renderState) {
        var equippedStyle = renderState.getGeckolibData(TICKET_EQUIPPED);
        if (equippedStyle.getStyle().isEmpty()) return Style.MISSING_MODEL_TEXTURE_ID;

        var style = equippedStyle.getStyle().get();
        if (style.hasVariants() && equippedStyle.getVariant() > -1) {
            return style.getTextureId(equippedStyle.getVariant());
        }

        return style.getTextureId();
    }

    @Override
    public @Nullable Animation getBakedAnimation(Style style, String animName) throws RuntimeException {
        if (style.getAnimationId().isEmpty()) return null;
        if (!GeckoLibResources.getBakedAnimations().cache().containsKey(style.getAnimationId().get())) return null;
        return GeckoLibResources.getBakedAnimations().cache().get(style.getAnimationId().get()).getAnimation(animName);
    }
    //? } else {
    /*@Override
    public @Nullable Animation getAnimation(Style style, String animName) {
        if (!GeckoLibCache.getBakedAnimations().containsKey(getAnimationResource(style)))
            return null;
        return super.getAnimation(style, animName);
    }
    *///? }
}
