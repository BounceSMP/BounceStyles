package dev.bsmp.bouncestyles.mixin.common;

import dev.bsmp.bouncestyles.core.pack.StylePackProvider;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(PackRepository.class)
public abstract class PackRepoMixin {
    @Shadow @Final @Mutable private Set<RepositorySource> sources;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void bounceStyles$registerStylePacks(RepositorySource[] repositorySources, CallbackInfo ci) {
        sources = new LinkedHashSet<>(sources);
        sources.add(StylePackProvider.INSTANCE);
    }

}
