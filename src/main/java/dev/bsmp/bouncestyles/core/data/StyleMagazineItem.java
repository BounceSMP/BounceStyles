package dev.bsmp.bouncestyles.core.data;

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

import java.util.List;
import java.util.Random;

public class StyleMagazineItem extends Item {
    public StyleMagazineItem() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        CompoundTag nbt = stack.getTag();
        if(nbt == null || !nbt.contains("styleId"))
            return;

        ResourceLocation styleId = ResourceLocation.tryParse(nbt.getString("styleId"));
        if(styleId == null)
            return;
        Style style = BounceStylesRegistries.getStyle(styleId);
        if(style == null)
            return;

        tooltip.add(Component.literal("Issue #" + nbt.getInt("issue")).withStyle(textStyle -> textStyle.withColor(ChatFormatting.GRAY).withItalic(true).withUnderlined(true)));
        for(BounceStylesRegistries.Category category : style.getCategories()) {
            tooltip.add(Component.literal("- ").append(Component.translatable(style.getStyleId().getNamespace()+"."+style.getStyleId().getPath()+"."+category.name().toLowerCase())).withStyle(
                    textStyle -> textStyle.withColor(ChatFormatting.GRAY))
            );
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if(!world.isClientSide) {
            if(StyleData.getOrCreateStyleData(user).unlockStyle(BounceStylesRegistries.getStyleIdFromStack(itemStack)) && !user.getAbilities().instabuild)
                itemStack.shrink(1);
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
        Random random = new Random();
        random.setSeed(styleId.toString().hashCode());

        CompoundTag nbt = new CompoundTag();
        nbt.putInt("issue", random.nextInt(1, BounceStylesRegistries.getAllStyleIds().size() + 1));
        nbt.putString("styleId", styleId.toString());

        itemStack.setTag(nbt);
        return itemStack;
    }
}
