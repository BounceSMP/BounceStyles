package dev.bsmp.bouncestyles.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.cache.GeckoLibResources;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletionStage;

@Mixin(GeckoLibResources.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class GeckoLibResourcesMixin {

    @Inject(method = "lambda$loadResources$15", at = @At("HEAD"))
    private static void bounceStyles$formatPath(CallbackInfoReturnable<CompletionStage> cir, @Local(argsOnly = true) LocalRef<Map<Identifier, Resource>> resources) {
        var updatedMap = new TreeMap<Identifier, Resource>();

        resources.get().forEach((identifier, resource) -> {
            if (identifier.getNamespace().equalsIgnoreCase(BounceStyles.modId)) {
                updatedMap.put(
                        identifier.withPath(
                                identifier.getPath()
                                        .replaceFirst("geo/", "")
                                        .replaceFirst("animations/", "")
                                        .replace(".geo.json", "")
                                        .replace(".animation.json", "")
                        ),
                        resource
                );
            }
        });

        resources.set(updatedMap);
    }

}
