package dev.bsmp.bouncestyles.core;

import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.bsmp.bouncestyles.core.commands.StyleCommand;
import dev.bsmp.bouncestyles.core.commands.StyleSlotArgumentType;
import dev.bsmp.bouncestyles.core.data.Style;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.data.StyleMagazineItem;
import dev.bsmp.bouncestyles.core.networking.BounceStylesNetwork;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.mixin.ArgumentTypesAccessor;
import dev.bsmp.bouncestyles.mixin.ChunkStorageAccessor;
import dev.bsmp.bouncestyles.mixin.EntityTrackerAccessor;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.chunk.ChunkSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

public class BounceStyles {
    public static final String modId = "bounce_styles";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceKey<Registry<Style>> STYLE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(modId, "styles"));
    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(modId));

    private static RegistrySupplier<StyleMagazineItem> MAGAZINE_ITEM;

    private static ServerLevel level;

    public static void init() {
        GeckoLib.initialize();
        ReloadListenerRegistry.register(PackType.SERVER_DATA, StyleLoader::loadStylePacks);

        BounceStylesNetwork.initServerbound();
        BounceStylesNetwork.initClientbound();

        Registrar<Item> items = REGISTRIES.get().get(Registries.ITEM);
        MAGAZINE_ITEM = items.register(resourceLocation("magazine"), StyleMagazineItem::new);

        CommandRegistrationEvent.EVENT.register((dispatcher, registry, dedicated) -> StyleCommand.register(dispatcher));

        Registrar<ArgumentTypeInfo<?, ?>> argTypes = REGISTRIES.get().get(Registries.COMMAND_ARGUMENT_TYPE);
        ArgumentTypeInfo<?, ?> serializer = SingletonArgumentInfo.contextFree(StyleSlotArgumentType::styleSlot);
        argTypes.register(resourceLocation("style_slot"), () -> serializer);
        ArgumentTypesAccessor.getClassMap().put(StyleSlotArgumentType.class, serializer);

        PlayerEvent.PLAYER_JOIN.register(BounceStyles::playerJoin);
        PlayerEvent.PLAYER_CLONE.register(StyleData::copyFrom);
        PlayerEvent.PLAYER_RESPAWN.register((player, conqueredEnd) -> new SyncStyleDataClientbound(player.getId(), StyleData.getOrCreateStyleData(player)).sendToPlayer(player));
        PlayerEvent.CHANGE_DIMENSION.register((player, oldLevel, newLevel) -> new SyncStyleDataClientbound(player.getId(), StyleData.getOrCreateStyleData(player)).sendToPlayer(player));

        StyleLoader.init();
    }

    public static StyleMagazineItem magazineItem() {
        return MAGAZINE_ITEM.get();
    }

    public static ServerLevel getLevel() {
        return level;
    }

    public static ResourceLocation resourceLocation(String path) {
        return new ResourceLocation(modId, path);
    }

    public static void playerJoin(ServerPlayer player) {
        SyncStyleDataClientbound packet = new SyncStyleDataClientbound(player.getId(), StyleData.getOrCreateStyleData(player));
        packet.sendToPlayer(player);
        packet.sendToTrackingPlayers(player);
    }

    public static void startTrackingPlayer(ServerPlayer tracker, ServerPlayer tracked) {
        new SyncStyleDataClientbound(tracker.getId(), StyleData.getOrCreateStyleData(tracker)).sendToPlayer(tracked);
        new SyncStyleDataClientbound(tracked.getId(), StyleData.getOrCreateStyleData(tracked)).sendToPlayer(tracker);
    }

    public static Set<ServerPlayerConnection> getPlayersTracking(Entity entity) {
        ChunkSource manager = entity.level().getChunkSource();
        if (manager instanceof ServerChunkCache) {
            ChunkMap storage = ((ServerChunkCache) manager).chunkMap;
            EntityTrackerAccessor tracker = ((ChunkStorageAccessor) storage).getEntityTrackers().get(entity.getId());

            if(tracker != null) {
                return tracker.getPlayersTracking();
            }
        }
        return Collections.emptySet();
    }
}
