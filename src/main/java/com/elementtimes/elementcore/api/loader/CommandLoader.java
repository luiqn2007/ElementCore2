package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.ModCommand;
import com.elementtimes.elementcore.api.helper.FindOptions;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.CommandSource;

import java.lang.annotation.ElementType;

/**
 * @author luqin2007
 */
public class CommandLoader {

    public static void load(ECModElements elements) {
        loadCommand(elements);
    }

    private static void loadCommand(ECModElements elements) {
        ObjHelper.stream(elements, ModCommand.class).forEach(data -> {
            FindOptions options = new FindOptions()
                    .withReturns(CommandNode.class, LiteralArgumentBuilder.class)
                    .withTypes(ElementType.TYPE, ElementType.FIELD)
                    .addParameterObjects(() -> new Object[0]);
            ObjHelper.find(elements, data, options).ifPresent(obj -> {
                if (obj instanceof CommandNode) {
                    elements.commands.add((CommandNode<CommandSource>) obj);
                } else {
                    elements.commandBuilders.add((LiteralArgumentBuilder<CommandSource>) obj);
                }
            });
        });
    }
}
