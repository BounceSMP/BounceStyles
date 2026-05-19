package dev.bsmp.bouncestyles.core.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.data.StyleMagazineItem;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class StyleCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> styleNode = Commands
                .literal("bouncestyles")
                //? if >= 1.21.11 {
                .requires(commandSourceStack -> commandSourceStack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                //? } else
//                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .build();
        dispatcher.getRoot().addChild(styleNode);

        unlockCommand(styleNode);
        removeCommand(styleNode);
        equipCommand(styleNode);
        itemizeCommand(styleNode);
    }

    //Register
    private static void unlockCommand(LiteralCommandNode<CommandSourceStack> styleNode) {
        LiteralCommandNode<CommandSourceStack> unlockNode = Commands
                .literal("unlock")
                .build();
        ArgumentCommandNode<CommandSourceStack, EntitySelector> playerNode = Commands
                .argument("players", EntityArgument.players())
                .build();
        LiteralCommandNode<CommandSourceStack> allNode = Commands
                .literal("all")
                .executes(context -> unlockAll(EntityArgument.getPlayers(context, "players")))
                .build();
        ArgumentCommandNode<CommandSourceStack, Identifier> unlockIdNode = Commands
                .argument("id", IdentifierArgument.id())
                .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(BounceStylesRegistries.getAllStyleIds(), builder))
                .executes(context -> unlock(EntityArgument.getPlayers(context, "players"), IdentifierArgument.getId(context, "id")))
                .build();

        styleNode.addChild(unlockNode);
        unlockNode.addChild(playerNode);
        playerNode.addChild(allNode);
        playerNode.addChild(unlockIdNode);
    }

    private static void removeCommand(LiteralCommandNode<CommandSourceStack> styleNode) {
        LiteralCommandNode<CommandSourceStack> removeNode = Commands
                .literal("remove")
                .build();
        ArgumentCommandNode<CommandSourceStack, EntitySelector> playerNode = Commands
                .argument("players", EntityArgument.players())
                .build();
        LiteralCommandNode<CommandSourceStack> allNode = Commands
                .literal("all")
                .executes(context -> removeAll(context.getSource(), EntityArgument.getPlayers(context, "players")))
                .build();
        ArgumentCommandNode<CommandSourceStack, Identifier> unlockIdNode = Commands
                .argument("id", IdentifierArgument.id())
                .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(BounceStylesRegistries.getAllStyleIds(), builder))
                .executes(context -> remove(context.getSource(), EntityArgument.getPlayers(context, "players"), IdentifierArgument.getId(context, "id")))
                .build();

        styleNode.addChild(removeNode);
        removeNode.addChild(playerNode);
        playerNode.addChild(allNode);
        playerNode.addChild(unlockIdNode);
    }

    private static void equipCommand(LiteralCommandNode<CommandSourceStack> styleNode) {
        LiteralCommandNode<CommandSourceStack> equipNode = Commands
                .literal("equip")
                .build();
        ArgumentCommandNode<CommandSourceStack, EntitySelector> playerNode = Commands
                .argument("player", EntityArgument.player())
                .build();
        ArgumentCommandNode<CommandSourceStack, Category> slotNode = Commands
                .argument("slot", StyleSlotArgumentType.styleSlot())
                .build();
        ArgumentCommandNode<CommandSourceStack, Identifier> idNode = Commands
                .argument("id", IdentifierArgument.id())
                .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(BounceStylesRegistries.getAllStyleIds(), builder))
                .executes(context -> equip(context, EntityArgument.getPlayer(context, "player"), StyleSlotArgumentType.getCategory(context, "slot"), IdentifierArgument.getId(context, "id")))
                .build();
        LiteralCommandNode<CommandSourceStack> emptyNode = Commands
                .literal("empty")
                .executes(context -> equip(context, EntityArgument.getPlayer(context, "player"), StyleSlotArgumentType.getCategory(context, "slot"), null))
                .build();


        styleNode.addChild(equipNode);
        equipNode.addChild(playerNode);
        playerNode.addChild(slotNode);
        slotNode.addChild(emptyNode);
        slotNode.addChild(idNode);
    }

    private static void itemizeCommand(LiteralCommandNode<CommandSourceStack> styleNode) {
        LiteralCommandNode<CommandSourceStack> itemizeNode = Commands
                .literal("itemize")
                .build();
        ArgumentCommandNode<CommandSourceStack, Identifier> idNode = Commands
                .argument("id", IdentifierArgument.id())
                .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(BounceStylesRegistries.getAllStyleIds(), builder))
                .executes(context -> itemize(Collections.singleton(context.getSource().getPlayer()), IdentifierArgument.getId(context, "id")))
                .build();
        ArgumentCommandNode<CommandSourceStack, EntitySelector> playerNode = Commands
                .argument("player", EntityArgument.players())
                .executes(context -> itemize(EntityArgument.getPlayers(context, "player"), IdentifierArgument.getId(context, "id")))
                .build();

        styleNode.addChild(itemizeNode);
        itemizeNode.addChild(idNode);
        idNode.addChild(playerNode);
    }

    //Functions
    private static int unlockAll(Collection<ServerPlayer> players) {
        for(ServerPlayer player : players) {
            StyleData styleData = StyleData.getOrCreateStyleData(player);
            for(Identifier id : BounceStylesRegistries.getAllStyleIds()) {
                styleData.unlockStyle(id);
            }
            player.displayClientMessage(Component.literal("You've unlocked all current styles, enjoy!").withStyle(style -> style.withColor(ChatFormatting.GOLD)), false);
        }
        return 1;
    }

    private static int unlock(Collection<ServerPlayer> players, Identifier id) {
        for(ServerPlayer player : players)
            if (id != null && BounceStylesRegistries.idExists(id)) {
                StyleData.getOrCreateStyleData(player).unlockStyle(id);
                player.displayClientMessage(Component.literal("Style unlocked").withStyle(style -> style.withColor(ChatFormatting.GOLD)), false);
            }
        return 1;
    }

    private static int removeAll(CommandSourceStack source, Collection<ServerPlayer> players) {
        for(ServerPlayer player : players) {
            StyleData styleData = StyleData.getOrCreateStyleData(player);
            for(Identifier id : BounceStylesRegistries.getAllStyleIds())
                if (styleData.hasStyleUnlocked(id))
                    styleData.removeStyle(id);
            source.sendSuccess(() -> Component.literal("Removed all styles for " + player.getScoreboardName()), true);
        }
        return 1;
    }

    private static int remove(CommandSourceStack source, Collection<ServerPlayer> players, Identifier id) {
        for(ServerPlayer player : players) {
            StyleData styleData = StyleData.getOrCreateStyleData(player);
            if (id != null && BounceStylesRegistries.idExists(id)) {
                if (styleData.hasStyleUnlocked(id)) {
                    styleData.removeStyle(id);
                    source.sendSuccess(() -> Component.literal("Removed style " + id + " from player " + player.getScoreboardName()), true);
                }
                else
                    source.sendFailure(Component.literal("Player does not have " + id + " unlocked"));
            }
        }
        return 1;
    }

    private static int equip(CommandContext<CommandSourceStack> context, ServerPlayer player, Category slot, Identifier id) {
        if(id == null || BounceStylesRegistries.idExists(id)) {
            Style style = id != null ? BounceStylesRegistries.getStyle(id).orElse(null) : null;
            if(style == null || style.getCategories().contains(slot)) {
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
                context.getSource().sendFailure(Component.literal("Given ID does not fit into " + slot.name() + " slot"));
        }
        else
            context.getSource().sendFailure(Component.literal("Given ID not found"));
        return 0;
    }

    private static int itemize(Collection<ServerPlayer> targets, Identifier styleId) {
        for(ServerPlayer player : targets) {
            ItemStack stack = StyleMagazineItem.createStackForStyle(styleId);
            if (!player.addItem(stack)) {
                ItemEntity itemEntity = player.drop(stack, false);
                if (itemEntity == null) continue;
                itemEntity.setNoPickUpDelay();
                itemEntity.setTarget(player.getUUID());
            }
        }
        return 0;
    }

}
