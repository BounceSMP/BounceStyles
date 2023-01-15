package dev.bsmp.bouncestyles;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import dev.bsmp.bouncestyles.item.StyleArmorMaterial;
import dev.bsmp.bouncestyles.item.StyleItem;
import dev.emi.trinkets.api.TrinketsApi;
import software.bernie.geckolib3.GeckoLib;

public class BounceStyles implements ModInitializer {
    public static final String modId = "bounce_styles";
    public static final ArmorMaterial STYLE_MATERIAL = new StyleArmorMaterial();

    public static final CreativeModeTab HEAD_GROUP = FabricItemGroupBuilder.build(new ResourceLocation(modId, "head"), () -> new ItemStack(Items.CHAINMAIL_HELMET));
    public static final CreativeModeTab BODY_GROUP = FabricItemGroupBuilder.build(new ResourceLocation(modId, "body"), () -> new ItemStack(Items.CHAINMAIL_CHESTPLATE));
    public static final CreativeModeTab LEGS_GROUP = FabricItemGroupBuilder.build(new ResourceLocation(modId, "legs"), () -> new ItemStack(Items.CHAINMAIL_LEGGINGS));
    public static final CreativeModeTab FEET_GROUP = FabricItemGroupBuilder.build(new ResourceLocation(modId, "feet"), () -> new ItemStack(Items.CHAINMAIL_BOOTS));

    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        TrinketsApi.registerTrinketPredicate(new ResourceLocation(modId, "item"), (itemStack, slotReference, livingEntity) -> {
            Item item = itemStack.getItem();
            if(item instanceof StyleItem) {
                switch (slotReference.inventory().getSlotType().getGroup()) {
                    case "head":
                        return item instanceof StyleItem.HeadStyleItem ? TriState.TRUE : TriState.FALSE;
                    case "chest":
                        return item instanceof StyleItem.BodyStyleItem ? TriState.TRUE : TriState.FALSE;
                    case "legs":
                        return item instanceof StyleItem.LegsStyleItem ? TriState.TRUE : TriState.FALSE;
                    case "feet":
                        return item instanceof StyleItem.FeetStyleItem ? TriState.TRUE : TriState.FALSE;
                }
            }
            return TriState.DEFAULT;
        });

        try {
            ItemLoader.init();
        }
        catch (IOException | NoSuchMethodException | InvocationTargetException | InstantiationException |
               IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
