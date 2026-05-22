package dev.bsmp.bouncestyles.core.item;

import dev.bsmp.bouncestyles.api.StyleEntity;
import dev.bsmp.bouncestyles.core.data.Category;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.data.Style;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.data.unlocks.UnlockManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

//? if >= 1.21.11 {
import net.minecraft.world.InteractionResult;
//? } else {
/*import net.minecraft.world.InteractionResultHolder;
*///? }
//? if >= 1.21.1 {
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
//? }

import java.util.List;

public class StyleMagazineItem extends Item {
    public StyleMagazineItem(Properties properties) {
        super(properties);
    }

    @Override
    //? if >= 1.21.11 {
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
    //? } else {
    /*public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
    *///? }
        ItemStack itemStack = user.getItemInHand(hand);
        if(!world.isClientSide()) {
            var styleId = getStyleIdFromStack(itemStack);
            if (!UnlockManager.unlockStyle(user, styleId) && styleId != null) {
                user.displayClientMessage(Component.literal("Style is already unlocked!").withStyle(ChatFormatting.RED), true);
                return InteractionResult.PASS;
            }
            if(!user.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            user.displayClientMessage(Component.literal("Style Unlocked"), true);
            return InteractionResult.CONSUME;
        }
        //? if >= 1.21.11 {
        return InteractionResult.PASS;
        //? } else {
        /*return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide);
        *///? }
    }

    public static ItemStack createStackForStyle(Style style) {
        if(style == null || style.getStyleId() == null)
            return null;
        return createStackForStyle(style.getStyleId());
    }

    public static ItemStack createStackForStyle(Identifier styleId) {
        ItemStack itemStack = new ItemStack(BounceStyles.magazineItem());
        //? if <= 1.20.1 {
        /*itemStack.getOrCreateTag().putString("style", styleId.toString());
        *///?} else if >= 1.21.1 {
        CustomData.update(DataComponents.CUSTOM_DATA, itemStack, compoundTag -> compoundTag.putString("style", styleId.toString()));
        //?}
        return itemStack;
    }

    public static Identifier getStyleIdFromStack(ItemStack itemStack) {
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
            //? if >= 1.21.11 {
            return Identifier.tryParse(tag.getString("style").get());
            //? } else {
            /*return Identifier.tryParse(tag.getString("style"));
            *///? }
        return null;
    }

    //? if >= 1.21.11 {

    //? } elif >= 1.21.1 {
    /*@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        appendTooltip(stack, tooltip);
    }
    *///? } else {
    /*@Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        appendTooltip(stack, tooltip);
    }
    *///? }


    private static void appendTooltip(ItemStack stack, List<Component> tooltip) {
        Identifier styleId = getStyleIdFromStack(stack);
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
