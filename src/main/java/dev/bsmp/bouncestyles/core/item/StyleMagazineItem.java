package dev.bsmp.bouncestyles.core.item;

import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.data.unlocks.UnlockManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;

//? if >= 1.21.10 {
import net.minecraft.world.InteractionResult;
//? } else {
/*import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.InteractionResultHolder;
*///? }

import java.util.ArrayList;
import java.util.List;

public class StyleMagazineItem extends Item {
    public StyleMagazineItem(Properties properties) {
        super(properties);
    }

    @Override
    //~ if >= 1.21.8 'InteractionResultHolder<ItemStack>' -> 'InteractionResult'
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if(!world.isClientSide()) {
            var styleId = getStyleIdFromStack(itemStack);
            if (UnlockManager.unlockStyle(user, styleId)) {
                if(!user.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                user.displayClientMessage(Component.literal("Style Unlocked"), true);

                return /*? if >= 1.21.8 { */ InteractionResult.CONSUME; /*? } else { */ /*InteractionResultHolder.consume(itemStack); *//*? } */
            }
            else
                user.displayClientMessage(Component.literal("Style is already unlocked!").withStyle(ChatFormatting.RED), true);
        }

        return /*? if >= 1.21.8 { */ InteractionResult.PASS; /*? } else { */ /*InteractionResultHolder.pass(itemStack); *//*? } */
    }

    public static ItemStack createStackForStyle(Style style) {
        if(style == null || style.getStyleId() == null)
            return null;
        return createStackForStyle(style.getStyleId());
    }

    public static ItemStack createStackForStyle(Identifier styleId) {
        ItemStack itemStack = new ItemStack(BounceStyles.magazineItem());
        //? if >= 1.21.1 {
            CustomData.update(DataComponents.CUSTOM_DATA, itemStack, compoundTag -> compoundTag.putString("style", styleId.toString()));
            var lore = new ArrayList<Component>();
            appendTooltip(styleId, lore);
            itemStack.applyComponents(DataComponentMap.builder().set(DataComponents.LORE, new ItemLore(lore)).build());
        //? } else {
        /*itemStack.getOrCreateTag().putString("style", styleId.toString());
        *///? }
        return itemStack;
    }

    public static Identifier getStyleIdFromStack(ItemStack itemStack) {
        //? if >= 1.21.1 {
        CompoundTag tag = null;
        var customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            tag = customData.copyTag();
        }
        //? } else {
        //CompoundTag tag = itemStack.getTag();
        //? }

        if (tag != null && tag.contains("style"))
            //? if >= 1.21.5 {
            return Identifier.tryParse(tag.getString("style").get());
            //? } else {
            /*return Identifier.tryParse(tag.getString("style"));
            *///? }
        return null;
    }

    //? if < 1.21.6 && >= 1.21.1 {
    /*@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        appendTooltip(getStyleIdFromStack(stack), tooltip);
    }
    *///? } elif < 1.21.1 {
    /*@Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        appendTooltip(getStyleIdFromStack(stack), tooltip);
    }
    *///? }

    private static void appendTooltip(Identifier styleId, List<Component> tooltip) {
        if (styleId == null) return;

        BounceStylesRegistries.getStyle(styleId).ifPresent(style -> {
            for (Category category : style.getCategories()) {
                tooltip.add(
                        Component.translatable(style.getStyleId().getNamespace() + "." + style.getStyleId().getPath() + "." + category.name().toLowerCase())
                                .withStyle((ChatFormatting.DARK_AQUA))
                                .append(Component.literal(" (" + category.name() + ")").withStyle(ChatFormatting.DARK_GRAY))
                );
            }
        });
    }
}
