package dev.bsmp.bouncestyles.core.data;

import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

//? if >= 1.21.1 {
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
//?}

import java.util.List;

public class StyleMagazineItem extends Item {
    public StyleMagazineItem() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if(!world.isClientSide) {
            if(StyleData.getOrCreateStyleData(user).unlockStyle(getStyleIdFromStack(itemStack)) && !user.getAbilities().instabuild) {
                user.displayClientMessage(Component.literal("Style Unlocked"), true);
                itemStack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide);
    }

    public static ItemStack createStackForStyle(Style style) {
        if(style == null || style.getStyleId() == null)
            return null;
        return createStackForStyle(style.getStyleId());
    }

    public static ItemStack createStackForStyle(ResourceLocation styleId) {
        ItemStack itemStack = new ItemStack(BounceStyles.magazineItem());
        //? if <= 1.20.1 {
        /*itemStack.getOrCreateTag().putString("style", styleId.toString());
        *///?} else if >= 1.21.1 {
        CustomData.update(DataComponents.CUSTOM_DATA, itemStack, compoundTag -> compoundTag.putString("style", styleId.toString()));
        //?}
        return itemStack;
    }

    public static ResourceLocation getStyleIdFromStack(ItemStack itemStack) {
        //? if <= 1.20.1 {
        /*CompoundTag tag = itemStack.getTag();
        *///?} else if >= 1.21.1 {
        CompoundTag tag = null;
        var customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            tag = customData.copyTag();
        }
        //?}

        if (tag != null && tag.contains("style"))
            return ResourceLocation.tryParse(tag.getString("style"));
        return null;
    }

    //? if <= 1.20.1 {
    /*@Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        appendTooltip(stack, tooltip);
    }
    *///?} else if >= 1.21.1 {
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        appendTooltip(stack, tooltip);
    }
    //?}

    private static void appendTooltip(ItemStack stack, List<Component> tooltip) {
        ResourceLocation styleId = getStyleIdFromStack(stack);
        if (styleId == null) return;

        BounceStylesRegistries.getStyle(styleId).ifPresent(style -> {
            for (Category category : style.getCategories()) {
                tooltip.add(Component.literal("- ").append(Component.translatable(style.getStyleId().getNamespace() + "." + style.getStyleId().getPath() + "." + category.name().toLowerCase())).withStyle(
                        textStyle -> textStyle.withColor(ChatFormatting.GRAY))
                );
            }
        });
    }
}
