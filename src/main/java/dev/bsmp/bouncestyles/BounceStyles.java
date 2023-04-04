package dev.bsmp.bouncestyles;

import dev.bsmp.bouncestyles.commands.StyleCommand;
import dev.bsmp.bouncestyles.commands.StyleSlotArgumentType;
import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.data.StyleMagazineItem;
import dev.bsmp.bouncestyles.networking.EquipStyleC2S;
import dev.bsmp.bouncestyles.networking.SyncStyleDataS2C;
import dev.bsmp.bouncestyles.networking.SyncStyleUnlocksBi;
import dev.bsmp.bouncestyles.networking.ToggleArmorVisibilityC2S;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import software.bernie.geckolib3.GeckoLib;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class BounceStyles implements ModInitializer {
    public static final String modId = "bounce_styles";
    public static final StyleMagazineItem MAGAZINE_ITEM = new StyleMagazineItem();

    @Override
    public void onInitialize() {
        try {
            GeckoLib.initialize();

            Registry.register(Registry.ITEM, new Identifier(modId, "magazine"), MAGAZINE_ITEM);
            CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> StyleCommand.register(dispatcher));
            ArgumentTypes.register("style_slot", StyleSlotArgumentType.class, new ConstantArgumentSerializer<>(StyleSlotArgumentType::styleSlot));

            ServerPlayNetworking.registerGlobalReceiver(SyncStyleUnlocksBi.ID_C2S, SyncStyleUnlocksBi::handleServer);
            ServerPlayNetworking.registerGlobalReceiver(EquipStyleC2S.ID, EquipStyleC2S::handle);
            ServerPlayNetworking.registerGlobalReceiver(ToggleArmorVisibilityC2S.ID, ToggleArmorVisibilityC2S::handle);

            EntityTrackingEvents.START_TRACKING.register(SyncStyleDataS2C::startTracking);
            ServerPlayConnectionEvents.JOIN.register(SyncStyleDataS2C::playerJoin);
            ServerPlayerEvents.COPY_FROM.register(StyleData::copyFrom);
            ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> SyncStyleDataS2C.sendToPlayer(player, player.getId(), StyleData.getPlayerData(player)));

            StyleLoader.init();
        }
        catch (IOException | NoSuchMethodException | InvocationTargetException | InstantiationException |
               IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
