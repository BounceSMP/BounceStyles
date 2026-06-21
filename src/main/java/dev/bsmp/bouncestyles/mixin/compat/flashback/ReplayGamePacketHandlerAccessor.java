//? if fabric {
package dev.bsmp.bouncestyles.mixin.compat.flashback;

import com.moulberry.flashback.playback.ReplayGamePacketHandler;
import com.moulberry.flashback.playback.ReplayServer;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ReplayGamePacketHandler.class)
@MixinEnvironment()
public interface ReplayGamePacketHandlerAccessor {
    @Invoker("getEntityOrPending") Entity invokeGetEntityOrPending(int entityId);
}
//? }