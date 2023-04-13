package dev.bsmp.bouncestyles;

import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.bsmp.bouncestyles.commands.StyleCommand;
import dev.bsmp.bouncestyles.commands.StyleSlotArgumentType;
import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.data.StyleMagazineItem;
import dev.bsmp.bouncestyles.mixin.ChunkStorageAccessor;
import dev.bsmp.bouncestyles.mixin.EntityTrackerAccessor;
import dev.bsmp.bouncestyles.networking.BounceStylesNetwork;
import dev.bsmp.bouncestyles.networking.packets.SyncStyleDataClientbound;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.ChunkManager;
import software.bernie.geckolib3.GeckoLib;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

public class BounceStyles {
    public static final String modId = "bounce_styles";
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(modId));

    public static RegistrySupplier<StyleMagazineItem> MAGAZINE_ITEM;

    public static void init() {
        GeckoLib.initialize();
        BounceStylesNetwork.initServerbound();
        BounceStylesNetwork.initClientbound();

        Registrar<Item> items = REGISTRIES.get().get(Registry.ITEM_KEY);
        MAGAZINE_ITEM = items.register(new Identifier(modId, "magazine"), StyleMagazineItem::new);

        CommandRegistrationEvent.EVENT.register((dispatcher, dedicated) -> StyleCommand.register(dispatcher));
        ArgumentTypes.register("style_slot", StyleSlotArgumentType.class, new ConstantArgumentSerializer<>(StyleSlotArgumentType::styleSlot));

        PlayerEvent.PLAYER_JOIN.register(BounceStyles::playerJoin);
        PlayerEvent.PLAYER_CLONE.register(StyleData::copyFrom);
        PlayerEvent.PLAYER_RESPAWN.register((player, conqueredEnd) -> new SyncStyleDataClientbound(player.getId(), StyleData.getOrCreateStyleData(player)).sendToPlayer(player));

        try {
            StyleLoader.init();
        }
        catch (IOException | NoSuchMethodException | InvocationTargetException | InstantiationException |
               IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static void playerJoin(ServerPlayerEntity player) {
        new SyncStyleDataClientbound(player.getId(), StyleData.getOrCreateStyleData(player)).sendToPlayer(player);
        for (EntityTrackingListener tracker : getPlayersTracking(player)) {
            if(player == tracker.getPlayer())
                continue;
            new SyncStyleDataClientbound(tracker.getPlayer().getId(), StyleData.getOrCreateStyleData(tracker.getPlayer())).sendToPlayer(player);
        }
    }

    public static void startTrackingPlayer(ServerPlayerEntity tracker, ServerPlayerEntity tracked) {
        new SyncStyleDataClientbound(tracker.getId(), StyleData.getOrCreateStyleData(tracker)).sendToPlayer(tracked);
        new SyncStyleDataClientbound(tracked.getId(), StyleData.getOrCreateStyleData(tracked)).sendToPlayer(tracker);
    }

    public static Set<EntityTrackingListener> getPlayersTracking(Entity entity) {
        ChunkManager manager = entity.world.getChunkManager();
        if (manager instanceof ServerChunkManager) {
            ThreadedAnvilChunkStorage storage = ((ServerChunkManager) manager).threadedAnvilChunkStorage;
            EntityTrackerAccessor tracker = ((ChunkStorageAccessor) storage).getEntityTrackers().get(entity.getId());

            if(tracker != null) {
                return tracker.getPlayersTracking();
            }
        }
        return Collections.emptySet();
    }
}