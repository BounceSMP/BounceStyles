package dev.bsmp.bouncestyles;

import dev.bsmp.bouncestyles.commands.StyleCommand;
import dev.bsmp.bouncestyles.networking.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import software.bernie.geckolib3.GeckoLib;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Mod(BounceStyles.modId)
@Mod.EventBusSubscriber(modid = BounceStyles.modId, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BounceStyles {
    public static final String modId = "bounce_styles";
    public static final SimpleChannel NETWORK = BounceStylesNetwork.NETWORK;

    @SubscribeEvent
    static void commonSetup(FMLCommonSetupEvent event) {
        try {
            GeckoLib.initialize();
            BounceStylesNetwork.init();
            StyleLoader.init();
        }
        catch (IOException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Mod.EventBusSubscriber(modid = BounceStyles.modId, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class EventHandler {

        @SubscribeEvent
        static void startTracking(PlayerEvent.StartTracking event) {
            SyncStyleDataS2C.startTracking(event.getTarget(), (ServerPlayerEntity) event.getPlayer());
        }

        @SubscribeEvent
        static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            SyncStyleDataS2C.playerJoin((ServerPlayerEntity) event.getPlayer());
        }

        @SubscribeEvent
        static void registerCommands(RegisterCommandsEvent event) {
            StyleCommand.register(event.getDispatcher());
        }

    }

}
