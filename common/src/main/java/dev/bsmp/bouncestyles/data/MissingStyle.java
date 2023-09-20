package dev.bsmp.bouncestyles.data;

import dev.bsmp.bouncestyles.BounceStyles;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class MissingStyle extends Style {
    public static final MissingStyle INSTANCE = new MissingStyle();

    private MissingStyle() {
        super(
                new Identifier(BounceStyles.modId, "missing_model"),
                new Identifier(BounceStyles.modId, "geo/missing_model.geo.json"),
                new Identifier(BounceStyles.modId, "textures/missing_model.png"),
                null, null
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {}
}
