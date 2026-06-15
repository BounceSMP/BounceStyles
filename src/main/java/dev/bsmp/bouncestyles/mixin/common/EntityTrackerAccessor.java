package dev.bsmp.bouncestyles.mixin.common;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import net.minecraft.server.network.ServerPlayerConnection;

@Mixin(targets = "net/minecraft/server/level/ChunkMap$TrackedEntity")
@MixinEnvironment
public interface EntityTrackerAccessor {
    @Accessor("seenBy") Set<ServerPlayerConnection> getPlayersTracking();
}
