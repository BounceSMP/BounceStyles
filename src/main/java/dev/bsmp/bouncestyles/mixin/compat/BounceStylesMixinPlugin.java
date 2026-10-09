package dev.bsmp.bouncestyles.mixin.compat;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class BounceStylesMixinPlugin implements IMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!mixinClassName.contains("compat.")) return true;
        var path = mixinClassName.split("\\.");
        var modId = path[path.length - 2];
        //? if fabric {
        return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded(modId);
        //? } else {
        /*//~ if >=1.21.11 'FMLLoader.getLoadingModList()' -> 'FMLLoader.getCurrent().getLoadingModList()'
        return net.neoforged.fml.loading.FMLLoader.getCurrent().getLoadingModList().getModFileById(modId) != null;
        *///? }
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
