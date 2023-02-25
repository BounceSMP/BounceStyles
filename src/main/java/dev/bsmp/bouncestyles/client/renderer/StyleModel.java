package dev.bsmp.bouncestyles.client.renderer;

import dev.bsmp.bouncestyles.data.Garment;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class StyleModel extends AnimatedGeoModel<Garment> {

    @Override
    public void setCustomAnimations(Garment animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
    }

    @Override
    public ResourceLocation getModelLocation(Garment garment) {
        return garment.modelID;
    }

    @Override
    public ResourceLocation getTextureLocation(Garment garment) {
        return garment.textureID;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Garment garment) {
        return garment.animationID;
    }

}
