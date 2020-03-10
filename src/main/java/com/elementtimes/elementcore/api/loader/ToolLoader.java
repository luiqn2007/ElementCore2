package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.part.Parts;
import com.elementtimes.elementcore.api.annotation.tools.Tooltips;
import com.elementtimes.elementcore.api.helper.ObjHelper;

public class ToolLoader {

    public static void load(ECModElements elements) {
        loadTooltips(elements);
    }

    private static void loadTooltips(ECModElements elements) {
        ObjHelper.stream(elements, Tooltips.class).forEach(data -> {
            elements.tooltips.add(Parts.tooltips(data.getAnnotationData(), null, elements));
        });
    }
}
