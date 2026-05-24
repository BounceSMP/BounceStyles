package dev.bsmp.bouncestyles.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.client.model.StyleModelFactory;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.cache.GeckoLibResources;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.loading.object.BakedModelFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(GeckoLibResources.class)
public abstract class GeckoLibResourcesMixin {

    @Redirect(method = "bakeModel", at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/loading/object/BakedModelFactory;getForNamespace(Ljava/lang/String;)Lsoftware/bernie/geckolib/loading/object/BakedModelFactory;"))
    private static BakedModelFactory bounceStyles$getStyleFactoryIfNeeded(String namespace, @Local(argsOnly = true, name = "path") Identifier path) {
        if (namespace.equalsIgnoreCase(BounceStyles.modId)) {
            return new StyleModelFactory(path);
        }
        return BakedModelFactory.getForNamespace(namespace);
    }

    @Inject(method = "reload", at = @At("TAIL"))
    private static void bounceStyles$addSplitMoels(CallbackInfoReturnable<CompletableFuture<Void>> cir, @Local(name = "models") LocalRef<CompletableFuture<Map<Identifier, BakedGeoModel>>> models) {
        models.set(models.get().thenApply(identifierBakedGeoModelMap -> {
            identifierBakedGeoModelMap.putAll(StyleModelFactory.separatedStyleModels);
            StyleModelFactory.separatedStyleModels.clear();
            return identifierBakedGeoModelMap;
        }));
    }
}
