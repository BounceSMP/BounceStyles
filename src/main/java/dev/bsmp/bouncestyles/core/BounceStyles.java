package dev.bsmp.bouncestyles.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.data.preset.ServerPresets;
import dev.bsmp.bouncestyles.core.data.style.StyleLoader;
import dev.bsmp.bouncestyles.core.item.StyleMagazineItem;
import dev.bsmp.bouncestyles.core.networking.StylesNetworking;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncPresetsClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BounceStyles {
    public static final String modId = "bounce_styles";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    private static RegistrySupplier<StyleMagazineItem> MAGAZINE_ITEM;

    public static void init() {
        StyleLoader.checkAndConvertPackFormat();

        BounceStylesRegistries.init();
        MAGAZINE_ITEM = BounceStylesRegistries.register(Registries.ITEM, id("magazine"), () -> {
            var properties = new Item.Properties().rarity(Rarity.RARE).stacksTo(1);
            //? if >= 1.21.11
            properties.setId(ResourceKey.create(Registries.ITEM, BounceStyles.id("magazine")));
            return new StyleMagazineItem(properties);
        });

        StylesNetworking.initServerbound();
        StylesNetworking.initClientbound();

        LifecycleEvent.SERVER_STARTING.register(server -> BounceStylesRegistries.setRegistryAccess(server.registryAccess()));
        LifecycleEvent.SERVER_STARTED.register(server ->
            BounceStylesRegistries.getRegistry().ifPresent(styles -> BounceStyles.LOGGER.info("Server Started with {} styles registered", styles.size()))
        );

        PlayerEvent.PLAYER_JOIN.register(player -> {
            new SyncStyleDataClientbound(player.getId(), StyleData.getEntityData(player)).sendToPlayer(player);
            ServerPresets.syncToPlayer(player);
        });
        PlayerEvent.PLAYER_CLONE.register((oldPlayer, newPlayer, wonGame) -> StyleData.copyFrom(oldPlayer, newPlayer));
        PlayerEvent.CHANGE_DIMENSION.register((player, oldLevel, newLevel) -> new SyncStyleDataClientbound(player.getId(), StyleData.getEntityData(player)).sendToPlayer(player));
        PlayerEvent.PLAYER_RESPAWN.register((player, conqueredEnd, reason) -> new SyncStyleDataClientbound(player.getId(), StyleData.getEntityData(player)).sendToPlayer(player));
    }

    public static void startTrackingPlayer(ServerPlayer tracker, ServerPlayer tracked) {
        new SyncStyleDataClientbound(tracker.getId(), StyleData.getEntityData(tracker)).sendToPlayer(tracked);
        new SyncStyleDataClientbound(tracked.getId(), StyleData.getEntityData(tracked)).sendToPlayer(tracker);
    }

    public static StyleMagazineItem magazineItem() {
        return MAGAZINE_ITEM.get();
    }

    public static Identifier id(String path) {
        if (!path.contains(":")) path = "%s:%s".formatted(modId, path);
        return Identifier.tryParse(path);
    }
}
