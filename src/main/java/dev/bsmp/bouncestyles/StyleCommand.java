package dev.bsmp.bouncestyles;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.bsmp.bouncestyles.data.PlayerStyleData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class StyleCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> styleNode = Commands
                .literal("style")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .build();
        dispatcher.getRoot().addChild(styleNode);

        LiteralCommandNode<CommandSourceStack> unlockNode = Commands
                .literal("unlock")
                .build();
        ArgumentCommandNode<CommandSourceStack, EntitySelector> playerNode = Commands
                .argument("players", EntityArgument.players())
                .build();
        LiteralCommandNode<CommandSourceStack> allNode = Commands
                .literal("all")
                .executes(context -> StyleCommand.unlockAll(context, EntityArgument.getPlayers(context, "players")))
                .build();
        ArgumentCommandNode<CommandSourceStack, String> idNode = Commands
                .argument("id", StringArgumentType.string())
                .executes(context -> StyleCommand.unlock(context, EntityArgument.getPlayers(context, "players"), ResourceLocationArgument.getId(context, "id")))
                .build();

        styleNode.addChild(unlockNode);
        unlockNode.addChild(playerNode);
        playerNode.addChild(allNode);
        playerNode.addChild(idNode);
    }

    private static int unlockAll(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players) {
        for(ServerPlayer player : players) {
            PlayerStyleData styleData = PlayerStyleData.getPlayerData(player);
            for(ResourceLocation id : GarmentLoader.UNIQUE_IDS) {
                styleData.unlockGarment(id);
            }
        }
        return 1;
    }

    private static int unlock(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, ResourceLocation id) {
        for(ServerPlayer player : targets)
            if (id != null && GarmentLoader.idExists(id))
                PlayerStyleData.getPlayerData(player).unlockGarment(id);
        return 1;
    }

}
