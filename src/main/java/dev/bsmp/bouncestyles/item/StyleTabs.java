package dev.bsmp.bouncestyles.item;

import dev.bsmp.bouncestyles.BounceStyles;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class StyleTabs {
    public static final CreativeModeTab HEAD_GROUP = new CreativeModeTab(BounceStyles.modId + ".head") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.CHAINMAIL_HELMET);
        }
    };

    public static final CreativeModeTab BODY_GROUP = new CreativeModeTab(BounceStyles.modId + ".body") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.CHAINMAIL_CHESTPLATE);
        }
    };

    public static final CreativeModeTab LEGS_GROUP = new CreativeModeTab(BounceStyles.modId + ".legs") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.CHAINMAIL_LEGGINGS);
        }
    };

    public static final CreativeModeTab FEET_GROUP = new CreativeModeTab(BounceStyles.modId + ".feet") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.CHAINMAIL_BOOTS);
        }
    };
}
