package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.BounceStyles;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class BounceStylesNetwork {
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new Identifier(BounceStyles.modId, "main"),
            () -> "1",
            "1"::equalsIgnoreCase,
            "1"::equalsIgnoreCase
    );

    public static void init() {
        int id = 0;
        //Bi 🏳️‍🌈
        NETWORK.registerMessage(id++, SyncStyleUnlocksBi.class, SyncStyleUnlocksBi::encode, SyncStyleUnlocksBi::decode, SyncStyleUnlocksBi::handle);

        //Client -> Server
        NETWORK.registerMessage(id++, EquipStyleC2S.class, EquipStyleC2S::encode, EquipStyleC2S::decode, EquipStyleC2S::handle);
        NETWORK.registerMessage(id++, ToggleArmorVisibilityC2S.class, ToggleArmorVisibilityC2S::encode, ToggleArmorVisibilityC2S::decode, ToggleArmorVisibilityC2S::handle);

        //Server -> Client
        NETWORK.registerMessage(id++, SyncStyleDataS2C.class, SyncStyleDataS2C::encode, SyncStyleDataS2C::decode, SyncStyleDataS2C::handle);
    }

}
