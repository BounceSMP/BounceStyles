package dev.bsmp.bouncestyles.data;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class StyleMagazineItem extends Item {
    public StyleMagazineItem() {
        super(new Settings().rarity(Rarity.RARE).maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();
        if(nbt == null || !nbt.contains("styleId"))
            return;

        Identifier styleId = Identifier.tryParse(nbt.getString("styleId"));
        if(styleId == null)
            return;
        Style style = StyleRegistry.getStyle(styleId);
        if(style == null)
            return;

        tooltip.add(new LiteralText("Issue #" + nbt.getInt("issue")).styled(textStyle -> textStyle.withColor(Formatting.GRAY).withItalic(true).withUnderline(true)));
        for(StyleRegistry.Category category : style.categories) {
            tooltip.add(new LiteralText("- ").append(new TranslatableText(style.styleId.getNamespace()+"."+style.styleId.getPath()+"."+category.name().toLowerCase())).styled(
                    textStyle -> textStyle.withColor(Formatting.GRAY))
            );
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if(!world.isClient) {
            if(StyleData.getOrCreateStyleData(user).unlockStyle(StyleRegistry.getStyleIdFromStack(itemStack)) && !user.getAbilities().creativeMode)
                itemStack.decrement(1);
        }
        return TypedActionResult.success(itemStack, world.isClient);
    }

    public static ItemStack createStackForStyle(Style style) {
        if(style == null || style.styleId == null)
            return null;
        return createStackForStyle(style.styleId);
    }

    public static ItemStack createStackForStyle(Identifier styleId) {
        ItemStack itemStack = new ItemStack(BounceStyles.MAGAZINE_ITEM.get());
        Random random = new Random();
        random.setSeed(styleId.toString().hashCode());

        NbtCompound nbt = new NbtCompound();
        nbt.putInt("issue", random.nextInt(1, StyleRegistry.REGISTRY.size() + 1));
        nbt.putString("styleId", styleId.toString());

        itemStack.setNbt(nbt);
        return itemStack;
    }
}
