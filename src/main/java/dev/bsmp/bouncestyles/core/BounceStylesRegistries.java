package dev.bsmp.bouncestyles.core;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.bsmp.bouncestyles.core.commands.StyleCommand;
import dev.bsmp.bouncestyles.core.commands.StyleSlotArgumentType;
import dev.bsmp.bouncestyles.core.data.*;
import dev.bsmp.bouncestyles.mixin.ArgumentTypesAccessor;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Supplier;

public class BounceStylesRegistries {
    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(BounceStyles.modId));

    public static final ResourceKey<Registry<Style>> STYLE_REGISTRY_KEY = ResourceKey.createRegistryKey(BounceStyles.resourceLocation("styles"));

    public static void init() {
        //Register Commands
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, dedicated) -> StyleCommand.register(dispatcher));

        //Register StyleSlot Command Argument Type
        Registrar<ArgumentTypeInfo<?, ?>> argTypes = REGISTRIES.get().get(Registries.COMMAND_ARGUMENT_TYPE);
        ArgumentTypeInfo<?, ?> serializer = SingletonArgumentInfo.contextFree(StyleSlotArgumentType::styleSlot);
        argTypes.register(BounceStyles.resourceLocation("style_slot"), () -> serializer);
        ArgumentTypesAccessor.getClassMap().put(StyleSlotArgumentType.class, serializer);
    }

    public static <T, E extends T> RegistrySupplier<E> register(ResourceKey<Registry<T>> key, ResourceLocation id, Supplier<E> supplier) {
        Registrar<T> registry = BounceStylesRegistries.REGISTRIES.get().get(key);
        return registry.register(BounceStyles.resourceLocation("magazine"), supplier);
    }

    //ToDo: This Stuff will likely get replaced once Styles are migrated to a Dynamic/Datapack Registry
    private static final HashMap<ResourceLocation, Style> REGISTRY = new HashMap<>();
    public static final HashMap<ResourceLocation, StylePreset> PRESETS = new HashMap<>();

//    public static void registerStyle(ResourceLocation id, Style style) {
//        if(id == null || style == null) {
//            BounceStyles.LOGGER.warn("Tried to register a Style with a null value. [id="+id+", style=" + style + "]");
//            return;
//        }
//        REGISTRY.put(id, style);
//    }

    public static Style getStyle(ResourceLocation id) {
        Style style = REGISTRY.get(id);
        if(style == null) style = MissingStyle.INSTANCE;
        return style;
    }

    @Nullable
    public static Style getStyleFromStack(ItemStack itemStack) {
        if(!(itemStack.getItem() instanceof StyleMagazineItem))
            return null;
        return getStyle(getStyleIdFromStack(itemStack));
    }

    static void clearRegistry() {
        REGISTRY.clear();
    }

    @Nullable public static ResourceLocation getStyleIdFromStack(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getTag();
        if(nbt == null || !nbt.contains("styleId"))
            return null;
        return ResourceLocation.tryParse(nbt.getString("styleId"));
    }

    public static Set<ResourceLocation> getAllStyleIds() {
        return REGISTRY.keySet();
    }

    public static Collection<Style> getAllStyles() {
        return REGISTRY.values();
    }

    public static StylePreset createPreset(StyleData styleData, String presetName) {
        StylePreset newPreset = styleData.createPreset(presetName);
        PRESETS.put(newPreset.presetId(), newPreset);
        StyleLoader.writePresetsFile();
        return newPreset;
    }

    public static boolean idExists(ResourceLocation id) {
        return REGISTRY.containsKey(id);
    }

    public static final ResourceLocation HEAD_ICON = BounceStyles.resourceLocation("textures/icon/bounce_head.png");
    public static final ResourceLocation BODY_ICON = BounceStyles.resourceLocation("textures/icon/bounce_body.png");
    public static final ResourceLocation LEGS_ICON = BounceStyles.resourceLocation("textures/icon/bounce_legs.png");
    public static final ResourceLocation FEET_ICON = BounceStyles.resourceLocation("textures/icon/bounce_feet.png");
    public static final ResourceLocation PRESET_ICON = BounceStyles.resourceLocation("textures/icon/bounce_preset.png");

    public enum Category implements StringRepresentable {
        Head(HEAD_ICON), Body(BODY_ICON), Legs(LEGS_ICON), Feet(FEET_ICON), Preset(PRESET_ICON);

        public final ResourceLocation categoryIcon;

        Category(ResourceLocation categoryIcon) {
            this.categoryIcon = categoryIcon;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }

        public static final Codec<Category> CODEC = StringRepresentable.fromEnum(Category::values);
    }
}
