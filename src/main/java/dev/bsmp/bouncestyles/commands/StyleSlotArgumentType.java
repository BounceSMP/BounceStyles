package dev.bsmp.bouncestyles.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.bsmp.bouncestyles.StyleLoader.Category;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StyleSlotArgumentType implements ArgumentType<Category> {
    private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType((found, constants) -> new TranslatableText("commands.forge.arguments.enum.invalid", new Object[]{constants, found}));

    private StyleSlotArgumentType() {}
    public static StyleSlotArgumentType styleSlot() { return new StyleSlotArgumentType(); }

    public static Category getCategory(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, Category.class);
    }

    @Override
    public Category parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();

        try {
            return Category.valueOf(name);
        } catch (IllegalArgumentException var4) {
            throw INVALID_ENUM.createWithContext(reader, name, Arrays.toString(Category.class.getEnumConstants()));
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Stream.of(Category.class.getEnumConstants()).filter(category -> category != Category.Preset).map(Object::toString), builder);
    }

    public Collection<String> getExamples() {
        return Stream.of(Category.class.getEnumConstants()).map(Object::toString).collect(Collectors.toList());
    }
}