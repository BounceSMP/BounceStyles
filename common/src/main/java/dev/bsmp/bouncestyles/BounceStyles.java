package dev.bsmp.bouncestyles;

import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.bsmp.bouncestyles.commands.StyleCommand;
import dev.bsmp.bouncestyles.commands.StyleSlotArgumentType;
import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.data.StyleMagazineItem;
import dev.bsmp.bouncestyles.networking.BounceStylesNetwork;
import dev.bsmp.bouncestyles.networking.packets.SyncStyleDataClientbound;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import software.bernie.geckolib3.GeckoLib;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class BounceStyles {
    public static final String modId = "bounce_styles";
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(modId));

    public static RegistrySupplier<StyleMagazineItem> MAGAZINE_ITEM;

    public static void init() {
        GeckoLib.initialize();
        BounceStylesNetwork.initServerbound();

        Registrar<Item> items = REGISTRIES.get().get(Registry.ITEM_KEY);
        MAGAZINE_ITEM = items.register(new Identifier(modId, "magazine"), StyleMagazineItem::new);

        CommandRegistrationEvent.EVENT.register((dispatcher, dedicated) -> StyleCommand.register(dispatcher));
        ArgumentTypes.register("style_slot", StyleSlotArgumentType.class, new ConstantArgumentSerializer<>(StyleSlotArgumentType::styleSlot));

        PlayerEvent.PLAYER_JOIN.register(SyncStyleDataClientbound::playerJoin);
        PlayerEvent.PLAYER_CLONE.register(StyleData::copyFrom);
        PlayerEvent.CHANGE_DIMENSION.register((player, origin, destination) -> new SyncStyleDataClientbound(player.getId(), StyleData.getPlayerData(player)).sendToPlayer(player));

        try {
            StyleLoader.init();
        }
        catch (IOException | NoSuchMethodException | InvocationTargetException | InstantiationException |
               IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
