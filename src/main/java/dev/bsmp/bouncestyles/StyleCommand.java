package dev.bsmp.bouncestyles;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.bsmp.bouncestyles.data.PlayerStyleData;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.TextComponent;
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
        ArgumentCommandNode<CommandSourceStack, ResourceLocation> idNode = Commands
                .argument("id", ResourceLocationArgument.id())
                .suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggestResource(StyleLoader.REGISTRY.keySet(), suggestionsBuilder))
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
            for(ResourceLocation id : StyleLoader.REGISTRY.keySet()) {
                styleData.unlockStyle(id);
            }
            player.sendMessage(new TextComponent("You've unlocked all current styles, enjoy!").withStyle(style -> style.withColor(ChatFormatting.GOLD)), null);
        }
        return 1;
    }

    private static int unlock(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, ResourceLocation id) {
        for(ServerPlayer player : targets)
            if (id != null && StyleLoader.idExists(id)) {
                PlayerStyleData.getPlayerData(player).unlockStyle(id);
                player.sendMessage(new TextComponent("Style unlocked").withStyle(style -> style.withColor(ChatFormatting.GOLD)), null);
            }
        return 1;
    }

}
