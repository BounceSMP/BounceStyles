//? if fabric {
/*package dev.bsmp.bouncestyles.fabric;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.StyleLoader;
import dev.bsmp.bouncestyles.core.commands.StyleCommand;
import dev.bsmp.bouncestyles.core.commands.StyleSlotArgumentType;
import dev.bsmp.bouncestyles.core.data.Style;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.data.StyleMagazineItem;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.core.pack.StylePackProvider;
import dev.bsmp.bouncestyles.mixin.ArgumentTypesAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static dev.bsmp.bouncestyles.core.BounceStyles.STYLE_REGISTRY_KEY;

public class BounceStylesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BounceStyles.init();
        ServerLifecycleEvents.SERVER_STARTED.register(StylePackProvider::registerToDataPacks);
    }

}
*///?}