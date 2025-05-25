package dev.bsmp.bouncestyles.core;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.data.StyleMagazineItem;
import dev.bsmp.bouncestyles.core.networking.BounceStylesNetwork;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.mixin.ChunkStorageAccessor;
import dev.bsmp.bouncestyles.mixin.EntityTrackerAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.Collections;
import java.util.Set;

public class BounceStyles {
    public static final String modId = "bounce_styles";
    public static final Logger LOGGER = LogManager.getLogger();

    private static RegistrySupplier<StyleMagazineItem> MAGAZINE_ITEM;

    public static void init() {
        GeckoLib.initialize();

        StyleLoader.checkAndConvertPackFormat();

        BounceStylesRegistries.init();
        MAGAZINE_ITEM = BounceStylesRegistries.register(Registries.ITEM, resourceLocation("magazine"), StyleMagazineItem::new);

        BounceStylesNetwork.initServerbound();
        BounceStylesNetwork.initClientbound();

        LifecycleEvent.SERVER_STARTING.register(server -> BounceStylesRegistries.setRegistryAccess(server.registryAccess()));
        LifecycleEvent.SERVER_STARTED.register(server -> BounceStylesRegistries.getRegistry().ifPresent(styles -> {
            BounceStyles.LOGGER.info("Server Started with {} styles registered", styles.size());
        }));

        PlayerEvent.PLAYER_JOIN.register(BounceStyles::playerJoin);
        PlayerEvent.PLAYER_CLONE.register((oldPlayer, newPlayer, wonGame) -> StyleData.copyFrom(oldPlayer, newPlayer));
        PlayerEvent.PLAYER_RESPAWN.register((player, conqueredEnd) -> new SyncStyleDataClientbound(player.getId(), StyleData.getOrCreateStyleData(player)).sendToPlayer(player));
        PlayerEvent.CHANGE_DIMENSION.register((player, oldLevel, newLevel) -> new SyncStyleDataClientbound(player.getId(), StyleData.getOrCreateStyleData(player)).sendToPlayer(player));
    }

    public static StyleMagazineItem magazineItem() {
        return MAGAZINE_ITEM.get();
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
