package dev.bsmp.bouncestyles.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.item.StyleItem;

@Mixin(ModelBakery.class)
public abstract class ModelLoaderMixin {
    private static final ResourceLocation STYLE_ITEM_BASE = new ResourceLocation(BounceStyles.modId, "item/bounce_item");

    @Shadow @Final private ResourceManager resourceManager;
    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Shadow protected abstract BlockModel loadBlockModel(ResourceLocation id) throws IOException;
    @Shadow protected abstract void cacheAndQueueDependencies(ResourceLocation id, UnbakedModel unbakedModel);

    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private void loadStyleModel(ResourceLocation id, CallbackInfo ci) {
        if(id instanceof ModelResourceLocation && Objects.equals(id.getNamespace(), BounceStyles.modId)) {
            ModelResourceLocation modelId = (ModelResourceLocation) id;
            ResourceLocation testId = new ResourceLocation(id.getNamespace(), "models/item/"+id.getPath().split("#")[0]+".json");
            if(!this.resourceManager.hasResource(testId)) {
//                JsonUnbakedModel model = BLOCK_ENTITY_MARKER; - For if I try to render the actual model at some point
                try {
                    BlockModel model = loadBlockModel(STYLE_ITEM_BASE);
                    this.cacheAndQueueDependencies(modelId, model);
                    this.unbakedCache.put(id, model);

                    Item item = Registry.ITEM.get(new ResourceLocation(BounceStyles.modId, id.getPath().split("#")[0]));
                    if(item instanceof StyleItem) {
                        ((StyleItem)item).useBackupModel = true;
                    }

                    ci.cancel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
