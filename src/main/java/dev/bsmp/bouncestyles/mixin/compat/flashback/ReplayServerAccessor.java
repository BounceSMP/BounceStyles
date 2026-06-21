//? if fabric {
package dev.bsmp.bouncestyles.mixin.compat.flashback;

import com.moulberry.flashback.playback.ReplayGamePacketHandler;
import com.moulberry.flashback.playback.ReplayServer;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ReplayServer.class)
@MixinEnvironment()
public interface ReplayServerAccessor {
    @Accessor("gamePacketHandler") ReplayGamePacketHandler getGamePacketHandler();
}
//? }