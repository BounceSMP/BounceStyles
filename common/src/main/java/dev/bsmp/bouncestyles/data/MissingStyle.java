package dev.bsmp.bouncestyles.data;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.HashMap;

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
    public void registerControllers(AnimationData animationData) {}
}
