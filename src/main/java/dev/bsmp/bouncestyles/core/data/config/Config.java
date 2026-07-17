package dev.bsmp.bouncestyles.core.data.config;

import com.bawnorton.configurable.Configurable;

import java.util.Arrays;

public class Config {
    /**
     * Whether the Unlock System should be used or not.
     * If true, Styles need to be Unlocked per-player in order for them to equip them.
     * Default: true
     */
    @Configurable(group = "unlocks")
    public static boolean requireUnlocks = true;

    /**
     * Whether a player must be in Creative mode, in addition to meeting the required Permission Level,
     * to bypass the Unlock system, and equip any Style.
     * Default: true
     */
    @Configurable(group = "unlocks")
    public static boolean unlockBypassRequiresCreative = true;

    /**
     * The permission level required by players to bypass the Unlock system, and equip any Style.
     * Relevant Wiki page for reference: https://minecraft.wiki/w/Permission_level#Java_Edition
     * Default: 2 (Gamemaster)
     */
    @Configurable(group = "unlocks")
    public static int unlockBypassPermissionLevel = 2;

    /**
     * Whether a button to open the Style GUI should appear in the Inventory GUI (in Survival mode)
     * Default: true
     */
    @Configurable(group = "inventory_button")
    public static boolean enableInventoryButton = true;

    /**
     * Specifies where the Style GUI button should be located within the Inventory GUI (in Survival mode), if enabled.
     * First value is the X position, the second value is the Y position, relative to the Top Left (0,0) of the Inventory GUI
     * Default: [126, 60]
     */
    @Configurable(group = "inventory_button", onSet = "onSetInventoryButtonPosition")
    public static int[] inventoryButtonPosition = { 126, 60 };

    public static void onSetInventoryButtonPosition(int[] value, boolean fromSync) {
        if (value.length < 2) {
            var x = value.length > 0 ? value[0] : 100;
            inventoryButtonPosition = new int[] { x, 100 };
        }
        else if (value.length > 2) {
            inventoryButtonPosition = Arrays.copyOfRange(value, 0, 2);
        }
    }
}
