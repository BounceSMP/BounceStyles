package dev.bsmp.bouncestyles.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StyleData;

import java.util.Collection;

import dev.bsmp.bouncestyles.networking.BounceStylesNetwork;
import dev.bsmp.bouncestyles.networking.SyncStyleDataS2C;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.network.PacketDistributor;

public class StyleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> styleNode = CommandManager
                .literal("style")
                .requires(commandSourceStack -> commandSourceStack.hasPermissionLevel(2))
                .build();
        dispatcher.getRoot().addChild(styleNode);

        //Unlock
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

        //Equip
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

    private static int unlock(Collection<ServerPlayerEntity> targets, Identifier id) {
        for(ServerPlayerEntity player : targets)
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
                    case Head: styleData.setHeadStyle(style); break;
                    case Body: styleData.setBodyStyle(style); break;
                    case Legs: styleData.setLegStyle(style); break;
                    case Feet: styleData.setFeetStyle(style); break;
                }
                SyncStyleDataS2C.sendToPlayer(player, player.getEntityId(), styleData);
                BounceStylesNetwork.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncStyleDataS2C(player.getEntityId(), styleData));
                return 1;
            }
            else
                context.getSource().sendError(new LiteralText("Given ID does not fit into " + slot.name() + " slot"));
        }
        else
            context.getSource().sendError(new LiteralText("Given ID not found"));
        return 0;
    }

}
