package dev.bsmp.bouncestyles.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.bsmp.bouncestyles.StyleRegistry;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.data.StyleMagazineItem;
import dev.bsmp.bouncestyles.networking.clientbound.SyncStyleDataClientbound;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;

public class StyleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> styleNode = CommandManager
                .literal("bouncestyles")
                .requires(commandSourceStack -> commandSourceStack.hasPermissionLevel(2))
                .build();
        dispatcher.getRoot().addChild(styleNode);

        registerUnlockCommand(styleNode);
        registerRemoveCommand(styleNode);
        registerEquipCommand(styleNode);
        registerItemCommand(styleNode);
    }

    //Register
    private static void registerUnlockCommand(LiteralCommandNode<ServerCommandSource> styleNode) {
        LiteralCommandNode<ServerCommandSource> unlockNode = CommandManager
                .literal("unlock")
                .build();
        ArgumentCommandNode<ServerCommandSource, EntitySelector> playerNode = CommandManager
                .argument("players", EntityArgumentType.players())
                .build();
        LiteralCommandNode<ServerCommandSource> allNode = CommandManager
                .literal("all")
                .executes(context -> unlockAll(EntityArgumentType.getPlayers(context, "players")))
                .build();
        ArgumentCommandNode<ServerCommandSource, Identifier> unlockIdNode = CommandManager
                .argument("id", IdentifierArgumentType.identifier())
                .suggests((context, builder) -> CommandSource.suggestIdentifiers(StyleRegistry.getAllStyleIds(), builder))
                .executes(context -> unlock(EntityArgumentType.getPlayers(context, "players"), IdentifierArgumentType.getIdentifier(context, "id")))
                .build();

        styleNode.addChild(unlockNode);
        unlockNode.addChild(playerNode);
        playerNode.addChild(allNode);
        playerNode.addChild(unlockIdNode);
    }

    private static void registerRemoveCommand(LiteralCommandNode<ServerCommandSource> styleNode) {
        LiteralCommandNode<ServerCommandSource> removeNode = CommandManager
                .literal("remove")
                .build();
        ArgumentCommandNode<ServerCommandSource, EntitySelector> playerNode = CommandManager
                .argument("players", EntityArgumentType.players())
                .build();
        LiteralCommandNode<ServerCommandSource> allNode = CommandManager
                .literal("all")
                .executes(context -> removeAll(context.getSource(), EntityArgumentType.getPlayers(context, "players")))
                .build();
        ArgumentCommandNode<ServerCommandSource, Identifier> unlockIdNode = CommandManager
                .argument("id", IdentifierArgumentType.identifier())
                .suggests((context, builder) -> CommandSource.suggestIdentifiers(StyleRegistry.getAllStyleIds(), builder))
                .executes(context -> remove(context.getSource(), EntityArgumentType.getPlayers(context, "players"), IdentifierArgumentType.getIdentifier(context, "id")))
                .build();

        styleNode.addChild(removeNode);
        removeNode.addChild(playerNode);
        playerNode.addChild(allNode);
        playerNode.addChild(unlockIdNode);
    }

    private static void registerEquipCommand(LiteralCommandNode<ServerCommandSource> styleNode) {
        LiteralCommandNode<ServerCommandSource> equipNode = CommandManager
                .literal("equip")
                .build();
        ArgumentCommandNode<ServerCommandSource, StyleRegistry.Category> slotNode = CommandManager
                .argument("slot", StyleSlotArgumentType.styleSlot())
                .build();

        LiteralCommandNode<ServerCommandSource> emptyNode = CommandManager
                .literal("empty")
                .build();
        ArgumentCommandNode<ServerCommandSource, EntitySelector> equipEmptyPlayerNode = CommandManager
                .argument("player", EntityArgumentType.player())
                .executes(context -> equip(context, EntityArgumentType.getPlayer(context, "player"), StyleSlotArgumentType.getCategory(context, "slot"), null))
                .build();

        ArgumentCommandNode<ServerCommandSource, Identifier> equipIdNode = CommandManager
                .argument("id", IdentifierArgumentType.identifier())
                .suggests((context, builder) -> CommandSource.suggestIdentifiers(StyleRegistry.getAllStyleIds(), builder))
                .build();
        ArgumentCommandNode<ServerCommandSource, EntitySelector> equipPlayerNode = CommandManager
                .argument("player", EntityArgumentType.player())
                .executes(context -> equip(context, EntityArgumentType.getPlayer(context, "player"), StyleSlotArgumentType.getCategory(context, "slot"), IdentifierArgumentType.getIdentifier(context, "id")))
                .build();

        styleNode.addChild(equipNode);
        equipNode.addChild(slotNode);

        slotNode.addChild(emptyNode);
        emptyNode.addChild(equipEmptyPlayerNode);

        slotNode.addChild(equipIdNode);
        equipIdNode.addChild(equipPlayerNode);
    }

    private static void registerItemCommand(LiteralCommandNode<ServerCommandSource> styleNode) {
        LiteralCommandNode<ServerCommandSource> itemizeNode = CommandManager
                .literal("itemize")
                .build();
        ArgumentCommandNode<ServerCommandSource, Identifier> idNode = CommandManager
                .argument("id", IdentifierArgumentType.identifier())
                .suggests((context, builder) -> CommandSource.suggestIdentifiers(StyleRegistry.getAllStyleIds(), builder))
                .executes(context -> itemize(Collections.singleton(context.getSource().getPlayer()), IdentifierArgumentType.getIdentifier(context, "id")))
                .build();
        ArgumentCommandNode<ServerCommandSource, EntitySelector> playerNode = CommandManager
                .argument("player", EntityArgumentType.players())
                .executes(context -> itemize(EntityArgumentType.getPlayers(context, "player"), IdentifierArgumentType.getIdentifier(context, "id")))
                .build();

        styleNode.addChild(itemizeNode);
        itemizeNode.addChild(idNode);
        idNode.addChild(playerNode);
    }

    //Functions
    private static int unlockAll(Collection<ServerPlayerEntity> players) {
        for(ServerPlayerEntity player : players) {
            StyleData styleData = StyleData.getOrCreateStyleData(player);
            for(Identifier id : StyleRegistry.getAllStyleIds()) {
                styleData.unlockStyle(id);
            }
            player.sendMessage(Text.literal("You've unlocked all current styles, enjoy!").styled(style -> style.withColor(Formatting.GOLD)), false);
        }
        return 1;
    }

    private static int unlock(Collection<ServerPlayerEntity> players, Identifier id) {
        for(ServerPlayerEntity player : players)
            if (id != null && StyleRegistry.idExists(id)) {
                StyleData.getOrCreateStyleData(player).unlockStyle(id);
                player.sendMessage(Text.literal("Style unlocked").styled(style -> style.withColor(Formatting.GOLD)), false);
            }
        return 1;
    }

    private static int removeAll(ServerCommandSource source, Collection<ServerPlayerEntity> players) {
        for(ServerPlayerEntity player : players) {
            StyleData styleData = StyleData.getOrCreateStyleData(player);
            for(Identifier id : StyleRegistry.getAllStyleIds()) {
                styleData.removeStyle(id);
            }
            source.sendFeedback(() -> Text.literal("Removed all styles for " + player.getEntityName()), true);
        }
        return 1;
    }

    private static int remove(ServerCommandSource source, Collection<ServerPlayerEntity> players, Identifier id) {
        for(ServerPlayerEntity player : players)
            if (id != null && StyleRegistry.idExists(id)) {
                StyleData.getOrCreateStyleData(player).removeStyle(id);
                source.sendFeedback(() -> Text.literal("Removed style " + id + " from player " + player.getEntityName()), true);
            }
        return 1;
    }

    private static int equip(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, StyleRegistry.Category slot, Identifier id) {
        if(id == null || StyleRegistry.idExists(id)) {
            Style style = id != null ? StyleRegistry.getStyle(id) : null;
            if(style == null || style.categories.contains(slot)) {
                StyleData styleData = StyleData.getOrCreateStyleData(player);
                switch (slot) {
                    case Head -> styleData.setHeadStyle(style);
                    case Body -> styleData.setBodyStyle(style);
                    case Legs -> styleData.setLegStyle(style);
                    case Feet -> styleData.setFeetStyle(style);
                }
                SyncStyleDataClientbound outPacket = new SyncStyleDataClientbound(player.getId(), styleData);
                outPacket.sendToPlayer(player);
                outPacket.sendToTrackingPlayers(player);
                return 1;
            }
            else
                context.getSource().sendError(Text.literal("Given ID does not fit into " + slot.name() + " slot"));
        }
        else
            context.getSource().sendError(Text.literal("Given ID not found"));
        return 0;
    }

    private static int itemize(Collection<ServerPlayerEntity> targets, Identifier styleId) {
        for(ServerPlayerEntity player : targets) {
            ItemStack stack = StyleMagazineItem.createStackForStyle(styleId);
            if (!player.giveItemStack(stack)) {
                ItemEntity itemEntity = player.dropItem(stack, false);
                if (itemEntity == null) continue;
                itemEntity.resetPickupDelay();
                itemEntity.setOwner(player.getUuid());
            }
        }
        return 0;
    }

}
