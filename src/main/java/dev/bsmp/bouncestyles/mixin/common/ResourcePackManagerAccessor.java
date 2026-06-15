package dev.bsmp.bouncestyles.mixin.common;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

@Mixin(PackRepository.class)
@MixinEnvironment
public interface ResourcePackManagerAccessor {
    @Accessor("sources") Set<RepositorySource> getProviders();
}
