package dev.bsmp.bouncestyles.core.data;

import dev.bsmp.bouncestyles.core.BounceStyles;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class MissingStyle extends Style {
    public static final MissingStyle INSTANCE = new MissingStyle();

    private MissingStyle() {
        super(
                BounceStyles.resourceLocation("missing_model"),
                BounceStyles.resourceLocation("geo/missing_model.geo.json"),
                BounceStyles.resourceLocation("textures/missing_model.png"),
                null, null
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {}
}
