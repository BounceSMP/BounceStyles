package dev.bsmp.bouncestyles.core;

import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.bsmp.bouncestyles.core.command.StyleCommand;
import dev.bsmp.bouncestyles.core.command.StyleSlotArgumentType;
import dev.bsmp.bouncestyles.core.data.style.Style;
import dev.bsmp.bouncestyles.core.item.StyleMagazineItem;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class BounceStylesRegistries {
    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(BounceStyles.modId));
    public static final ResourceKey<Registry<Style>> STYLE_REGISTRY_KEY = ResourceKey.createRegistryKey(BounceStyles.id("styles"));

    private static RegistryAccess registryAccess;

    public static void init() {
        //Register Commands
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, dedicated) -> StyleCommand.register(dispatcher));

        //Register StyleSlot Command Argument Type
        //? if fabric {
        net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry.registerArgumentType(
                BounceStyles.id("style_slot"),
                StyleSlotArgumentType.class,
                SingletonArgumentInfo.contextFree(StyleSlotArgumentType::styleSlot)
        );
        //?} else {
//        Registrar<ArgumentTypeInfo<?, ?>> argTypes = REGISTRIES.get().get(Registries.COMMAND_ARGUMENT_TYPE);
//        var argumentTypeInfo = ArgumentTypeInfos.registerByClass(StyleSlotArgumentType.class, SingletonArgumentInfo.contextFree(StyleSlotArgumentType::styleSlot));
//        argTypes.register(BounceStyles.id("style_slot"), () -> argumentTypeInfo);
        //?}
    }

    public static <T, E extends T> RegistrySupplier<E> register(ResourceKey<Registry<T>> key, Identifier id, Supplier<E> supplier) {
        Registrar<T> registry = BounceStylesRegistries.REGISTRIES.get().get(key);
        return registry.register(id, supplier);
    }

    public static Optional<Registry<Style>> getRegistry() {
        if (registryAccess == null) return Optional.empty();
        //? if <= 1.20.1 {
        //return registryAccess.registry(STYLE_REGISTRY_KEY);
        //? } else {
        return registryAccess.lookup(STYLE_REGISTRY_KEY);
        //? }
    }

    public static Optional<Style> getStyle(Identifier id) {
        //? if <= 1.20.1 {
        //return getRegistry().map(styles -> styles.get(id));
        //? } else {
        return getRegistry().flatMap(styles -> styles.get(id).map(Holder.Reference::value));
        //? }
    }

    @Nullable
    public static Optional<Style> getStyleFromStack(ItemStack itemStack) {
        if(!(itemStack.getItem() instanceof StyleMagazineItem))
            return null;
        return getStyle(StyleMagazineItem.getStyleIdFromStack(itemStack));
    }

    public static Set<Identifier> getAllStyleIds() {
        return getRegistry().map(registry -> registry.keySet()).orElse(Set.of());
    }

    public static Collection<Style> getAllStyles() {
        return getRegistry().map(registry -> registry.stream().toList()).orElse(List.of());
    }

    public static boolean idExists(Identifier id) {
        return getRegistry().map(registry -> registry.containsKey(id)).orElse(false);
    }

    public static void setRegistryAccess(RegistryAccess access) {
        registryAccess = access;
    }

}
