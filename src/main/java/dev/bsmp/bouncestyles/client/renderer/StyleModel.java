package dev.bsmp.bouncestyles.client.renderer;

import dev.bsmp.bouncestyles.data.Style;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class StyleModel extends AnimatedGeoModel<Style> {

    @Override
    public void setCustomAnimations(Style animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
    }

    @Override
    public Identifier getModelLocation(Style style) {
        return style.modelID;
    }

    @Override
    public Identifier getTextureLocation(Style style) {
        return style.textureID;
    }

    @Override
    public Identifier getAnimationFileLocation(Style style) {
        return style.animationID;
    }

}
