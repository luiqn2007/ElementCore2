package com.elementtimes.elementcore.api.common.loader;

import com.elementtimes.elementcore.api.annotation.ModGui;
import com.elementtimes.elementcore.api.common.ECModElements;
import com.elementtimes.elementcore.api.common.helper.ObjHelper;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * @author luqin2007
 */
public class GuiLoader {

    public static void load(ECModElements elements) {
        elements.warn("[COMMAND]load gui");
        ObjHelper.stream(elements, ModGui.class)
                .forEach(data -> ObjHelper.find(elements, IGuiHandler.class, data).ifPresent(h -> {
                    elements.guiHandler = h;
                    elements.warn("[ModGui]{}", h);
                }));
        elements.warn("[COMMAND]load gui finished");
    }
}
