package dev.bsmp.bouncestyles.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StyleData;

import java.util.Collection;
import java.util.Collections;

import dev.bsmp.bouncestyles.data.StyleMagazineItem;
import dev.bsmp.bouncestyles.networking.SyncStyleDataS2C;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class StyleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> styleNode = CommandManager
                .literal("bouncestyles")
                .requires(commandSourceStack -> commandSourceStack.hasPermissionLevel(2))
                .build();
        dispatcher.getRoot().addChild(styleNode);

        registerUnlockCommand(styleNode);
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
                .suggests((context, builder) -> CommandSource.suggestIdentifiers(StyleLoader.REGISTRY.keySet(), builder))
                .executes(context -> unlock(EntityArgumentType.getPlayers(context, "players"), IdentifierArgumentType.getIdentifier(context, "id")))
                .build();
        styleNode.addChild(unlockNode);
        unlockNode.addChild(playerNode);
        playerNode.addChild(allNode);
        playerNode.addChild(unlockIdNode);
    }

    private static void registerEquipCommand(LiteralCommandNode<ServerCommandSource> styleNode) {
        LiteralCommandNode<ServerCommandSource> equipNode = CommandManager
                .literal("equip")
                .build();
        ArgumentCommandNode<ServerCommandSource, StyleLoader.Category> slotNode = CommandManager
                .argument("slot", StyleSlotArgumentType.styleSlot())
                .build();
        ArgumentCommandNode<ServerCommandSource, Identifier> equipIdNode = CommandManager
                .argument("id", IdentifierArgumentType.identifier())
                .suggests((context, builder) -> CommandSource.suggestIdentifiers(StyleLoader.REGISTRY.keySet(), builder))
                .build();
        ArgumentCommandNode<ServerCommandSource, EntitySelector> equipPlayerNode = CommandManager
                .argument("player", EntityArgumentType.player())
                .executes(context -> equip(context, EntityArgumentType.getPlayer(context, "player"), StyleSlotArgumentType.getCategory(context, "slot"), IdentifierArgumentType.getIdentifier(context, "id")))
                .build();
        styleNode.addChild(equipNode);
        equipNode.addChild(slotNode);
        slotNode.addChild(equipIdNode);
        equipIdNode.addChild(equipPlayerNode);
    }

    private static void registerItemCommand(LiteralCommandNode<ServerCommandSource> styleNode) {
        LiteralCommandNode<ServerCommandSource> itemizeNode = CommandManager
                .literal("itemize")
                .build();
        ArgumentCommandNode<ServerCommandSource, Identifier> idNode = CommandManager
                .argument("id", IdentifierArgumentType.identifier())
                .suggests((context, builder) -> CommandSource.suggestIdentifiers(StyleLoader.REGISTRY.keySet(), builder))
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
            StyleData styleData = StyleData.getPlayerData(player);
            for(Identifier id : StyleLoader.REGISTRY.keySet()) {
                styleData.unlockStyle(id);
            }
            player.sendSystemMessage(new LiteralText("You've unlocked all current styles, enjoy!").styled(style -> style.withColor(Formatting.GOLD)), null);
        }
        return 1;
    }

    private static int unlock(Collection<ServerPlayerEntity> players, Identifier id) {
        for(ServerPlayerEntity player : players)
            if (id != null && StyleLoader.idExists(id)) {
                StyleData.getPlayerData(player).unlockStyle(id);
                player.sendSystemMessage(new LiteralText("Style unlocked").styled(style -> style.withColor(Formatting.GOLD)), null);
            }
        return 1;
    }

    private static int equip(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, StyleLoader.Category slot, Identifier id) {
        if(StyleLoader.idExists(id)) {
            Style style = StyleLoader.getStyle(id);
            if(style.categories.contains(slot)) {
                StyleData styleData = StyleData.getPlayerData(player);
                switch (slot) {
                    case Head -> styleData.setHeadStyle(style);
                    case Body -> styleData.setBodyStyle(style);
                    case Legs -> styleData.setLegStyle(style);
                    case Feet -> styleData.setFeetStyle(style);
                }
                SyncStyleDataS2C.sendToPlayer(player, player.getId(), styleData);
                for(ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(player)) {
                    SyncStyleDataS2C.sendToPlayer(trackingPlayer, player.getId(), styleData);
                }
                return 1;
            }
            else
                context.getSource().sendError(new LiteralText("Given ID does not fit into " + slot.name() + " slot"));
        }
        else
            context.getSource().sendError(new LiteralText("Given ID not found"));
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
