package dev.bsmp.bouncestyles.client.renderer;

import dev.bsmp.bouncestyles.data.Style;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StyleModel extends GeoModel<Style> {
    @Override
    public ResourceLocation getModelResource(Style style) {
        return style.modelID;
    }

    @Override
    public ResourceLocation getTextureResource(Style style) {
        return style.textureID;
    }

    @Override
    public ResourceLocation getAnimationResource(Style style) {
        return style.animationID;
    }
}
