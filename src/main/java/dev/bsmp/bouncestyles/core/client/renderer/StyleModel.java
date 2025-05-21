package dev.bsmp.bouncestyles.core.client.renderer;

import dev.bsmp.bouncestyles.core.data.Style;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StyleModel extends GeoModel<Style> {
    @Override
    public ResourceLocation getModelResource(Style style) {
        return style.getModelId();
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
