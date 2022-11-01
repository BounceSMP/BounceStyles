package dev.bsmp.bouncestyles.mixin;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.item.StyleItem;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    private static final Identifier STYLE_ITEM_BASE = new Identifier(BounceStyles.modId, "item/bounce_item");

    @Shadow @Final private ResourceManager resourceManager;
    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;

    @Shadow protected abstract JsonUnbakedModel loadModelFromJson(Identifier id) throws IOException;
    @Shadow protected abstract void putModel(Identifier id, UnbakedModel unbakedModel);

    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private void loadStyleModel(Identifier id, CallbackInfo ci) {
        if(id instanceof ModelIdentifier && Objects.equals(id.getNamespace(), BounceStyles.modId)) {
            ModelIdentifier modelId = (ModelIdentifier) id;
            Identifier testId = new Identifier(id.getNamespace(), "models/item/"+id.getPath().split("#")[0]+".json");
            if(!this.resourceManager.containsResource(testId)) {
//                JsonUnbakedModel model = BLOCK_ENTITY_MARKER; - For if I try to render the actual model at some point
                try {
                    JsonUnbakedModel model = loadModelFromJson(STYLE_ITEM_BASE);
                    this.putModel(modelId, model);
                    this.unbakedModels.put(id, model);

                    //ToDo: WHY THIS NO WORK??
                    Item item = Registry.ITEM.get(new Identifier(BounceStyles.modId, id.getPath().split("#")[0]));
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
