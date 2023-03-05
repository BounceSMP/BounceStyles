package dev.bsmp.bouncestyles;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.bsmp.bouncestyles.data.StyleData;
import java.util.Collection;
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

public class StyleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> styleNode = CommandManager
                .literal("style")
                .requires(commandSourceStack -> commandSourceStack.hasPermissionLevel(2))
                .build();
        dispatcher.getRoot().addChild(styleNode);

        LiteralCommandNode<ServerCommandSource> unlockNode = CommandManager
                .literal("unlock")
                .build();
        ArgumentCommandNode<ServerCommandSource, EntitySelector> playerNode = CommandManager
                .argument("players", EntityArgumentType.players())
                .build();
        LiteralCommandNode<ServerCommandSource> allNode = CommandManager
                .literal("all")
                .executes(context -> StyleCommand.unlockAll(context, EntityArgumentType.getPlayers(context, "players")))
                .build();
        ArgumentCommandNode<ServerCommandSource, Identifier> idNode = CommandManager
                .argument("id", IdentifierArgumentType.identifier())
                .suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(StyleLoader.REGISTRY.keySet(), suggestionsBuilder))
                .executes(context -> StyleCommand.unlock(context, EntityArgumentType.getPlayers(context, "players"), IdentifierArgumentType.getIdentifier(context, "id")))
                .build();

        styleNode.addChild(unlockNode);
        unlockNode.addChild(playerNode);
        playerNode.addChild(allNode);
        playerNode.addChild(idNode);
    }

    private static int unlockAll(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> players) {
        for(ServerPlayerEntity player : players) {
            StyleData styleData = StyleData.getPlayerData(player);
            for(Identifier id : StyleLoader.REGISTRY.keySet()) {
                styleData.unlockStyle(id);
            }
            player.sendSystemMessage(new LiteralText("You've unlocked all current styles, enjoy!").styled(style -> style.withColor(Formatting.GOLD)), null);
        }
        return 1;
    }

    private static int unlock(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, Identifier id) {
        for(ServerPlayerEntity player : targets)
            if (id != null && StyleLoader.idExists(id)) {
                StyleData.getPlayerData(player).unlockStyle(id);
                player.sendSystemMessage(new LiteralText("Style unlocked").styled(style -> style.withColor(Formatting.GOLD)), null);
            }
        return 1;
    }

}
