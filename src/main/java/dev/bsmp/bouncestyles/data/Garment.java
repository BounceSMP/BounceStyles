package dev.bsmp.bouncestyles.data;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.List;

public class Garment implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public final ResourceLocation modelID;
    public final ResourceLocation textureID;
    @Nullable
    public final ResourceLocation animationID;
    @Nullable public final HashMap<String, String> animationMap;

    public List<String> hiddenParts;
    public int transitionTicks;

    public Garment(ResourceLocation modelID, ResourceLocation textureID, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap) {
        this.modelID = modelID;
        this.textureID = textureID;
        this.animationID = animationID;
        this.animationMap = animationMap;
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
