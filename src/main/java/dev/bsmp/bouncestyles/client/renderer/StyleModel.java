package dev.bsmp.bouncestyles.client.renderer;

import net.minecraft.util.Identifier;

import dev.bsmp.bouncestyles.item.StyleItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class StyleModel extends AnimatedGeoModel<StyleItem> {

    @Override
    public Identifier getModelLocation(StyleItem item) {
        return item.modelID;
    }

    @Override
    public Identifier getTextureLocation(StyleItem item) {
        return item.textureID;
    }

    @Override
    public Identifier getAnimationFileLocation(StyleItem item) {
        return item.animationID;
    }

}
