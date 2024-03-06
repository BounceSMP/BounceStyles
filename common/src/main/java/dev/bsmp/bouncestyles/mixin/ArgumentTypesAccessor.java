package dev.bsmp.bouncestyles.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;

@Mixin(ArgumentTypeInfos.class)
public interface ArgumentTypesAccessor {
    @Accessor("BY_CLASS")
    static Map<Class<?>, ArgumentTypeInfo<?, ?>> getClassMap() {
        throw new AssertionError();
    }
}
