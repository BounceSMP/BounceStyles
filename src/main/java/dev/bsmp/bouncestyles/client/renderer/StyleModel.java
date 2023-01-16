package dev.bsmp.bouncestyles.client.renderer;

import dev.bsmp.bouncestyles.item.StyleItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class StyleModel extends AnimatedGeoModel<StyleItem> {

    @Override
    public ResourceLocation getModelLocation(StyleItem item) {
        return item != null ? item.modelID : null;
    }

    @Override
    public ResourceLocation getTextureLocation(StyleItem item) {
        return  item != null ? item.textureID : null;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(StyleItem item) {
        return  item != null ? item.animationID : null;
    }

}
