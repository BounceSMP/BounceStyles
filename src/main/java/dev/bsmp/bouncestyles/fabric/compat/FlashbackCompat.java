//? if fabric {
package dev.bsmp.bouncestyles.fabric.compat;

import com.moulberry.flashback.action.Action;
import com.moulberry.flashback.action.ActionRegistry;
import com.moulberry.flashback.playback.ReplayServer;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.networking.ClientPacketHandler;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class FlashbackCompat {

    public static void init() {
        ActionRegistry.register(StyleDataAction.INSTANCE);
    }

    public static class StyleDataAction implements Action {
        public static final StyleDataAction INSTANCE = new StyleDataAction();

        private StyleDataAction() {}

        @Override
        public Identifier name() {
            return BounceStyles.id("style_data_action");
        }

        @Override
        public void handle(ReplayServer replayServer, RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            var packet = SyncStyleDataClientbound.STREAM_CODEC.decode(registryFriendlyByteBuf);
            replayServer.getAllLevels().forEach(level -> {
                if (level.getEntity(packet.entityId()) instanceof Player player) {
                    StyleData.setEntityData(player, packet.styleData());
                }
            });
        }
    }

}
//? }