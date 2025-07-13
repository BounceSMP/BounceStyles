//? if neoforge {
package dev.bsmp.bouncestyles.neoforge;

import com.mojang.serialization.Lifecycle;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.ClientPacketHandler;
import dev.bsmp.bouncestyles.core.networking.ServerPacketHandler;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import dev.bsmp.bouncestyles.core.networking.clientbound.OpenWardrobeUIClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.ToggleArmorVisibilityServerbound;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@Mod(BounceStyles.modId)
@EventBusSubscriber(modid = BounceStyles.modId, bus = EventBusSubscriber.Bus.MOD)
public class BounceStylesNeoforge<T extends StylePacket> implements IPayloadHandler<T> {
    private static BounceStylesNeoforge instance;

    public BounceStylesNeoforge() {
        BounceStyles.init();
        instance = this;
    }

    @SubscribeEvent
    public static void registerDynamicRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BounceStylesRegistries.STYLE_REGISTRY_KEY, Style.CODEC.withLifecycle(Lifecycle.stable()));
    }

    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("300");

        //Serverbound
        registrar.playToServer(EquipStyleServerbound.TYPE, EquipStyleServerbound.STREAM_CODEC, instance);
        registrar.playToServer(OpenStyleScreenServerbound.TYPE, OpenStyleScreenServerbound.STREAM_CODEC, instance);
        registrar.playToServer(ToggleArmorVisibilityServerbound.TYPE, ToggleArmorVisibilityServerbound.STREAM_CODEC, instance);

        //Clientbound
        registrar.playToClient(OpenWardrobeUIClientbound.TYPE, OpenWardrobeUIClientbound.STREAM_CODEC, instance);
        registrar.playToClient(SyncStyleDataClientbound.TYPE, SyncStyleDataClientbound.STREAM_CODEC, instance);
    }

    @Override
    public void handle(StylePacket packetIn, IPayloadContext context) {
        //Serverbound
        if (packetIn instanceof EquipStyleServerbound packet) context.enqueueWork(() -> ServerPacketHandler.handleEquipStyle((ServerPlayer) context.player(), packet));
        else if (packetIn instanceof OpenStyleScreenServerbound packet) context.enqueueWork(() -> new OpenWardrobeUIClientbound(StyleData.getOrCreateStyleData(context.player()).getUnlocks()).sendToPlayer((ServerPlayer) context.player()));
        else if (packetIn instanceof ToggleArmorVisibilityServerbound packet) context.enqueueWork(() -> ServerPacketHandler.handleArmorVisibility((ServerPlayer) context.player(), packet));

        //ClientBound
        else if (packetIn instanceof OpenWardrobeUIClientbound packet) context.enqueueWork(() -> ClientPacketHandler.handleOpenWardrobeUI(context.player(), packet));
        else if (packetIn instanceof SyncStyleDataClientbound packet) context.enqueueWork(() -> ClientPacketHandler.handleSyncStyleData(packet));
    }
}
//?}