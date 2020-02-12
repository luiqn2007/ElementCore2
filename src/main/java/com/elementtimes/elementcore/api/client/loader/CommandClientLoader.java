package com.elementtimes.elementcore.api.client.loader;

import com.elementtimes.elementcore.api.annotation.ModCommand;
import com.elementtimes.elementcore.api.common.ECModElements;
import com.elementtimes.elementcore.api.common.helper.ObjHelper;
import net.minecraft.command.ICommand;

/**
 * @author luqin2007
 */
public class CommandClientLoader {

    public static void load(ECModElements elements) {
        ObjHelper.stream(elements, ModCommand.class).forEach(data -> {
            if ((boolean) data.getAnnotationInfo().getOrDefault("client", false)) {
                ObjHelper.findOrNew(elements, ICommand.class, data).ifPresent(elements.getClientNotInit().commands::add);
            }
        });
    }
}