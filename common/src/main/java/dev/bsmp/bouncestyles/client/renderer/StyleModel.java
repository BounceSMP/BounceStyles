package dev.bsmp.bouncestyles.client.renderer;

import dev.bsmp.bouncestyles.data.Style;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class StyleModel extends GeoModel<Style> {
    @Override
    public Identifier getModelResource(Style style) {
        return style.modelID;
    }

    @Override
    public Identifier getTextureResource(Style style) {
        return style.textureID;
    }

    @Override
    public Identifier getAnimationResource(Style style) {
        return style.animationID;
    }
}
