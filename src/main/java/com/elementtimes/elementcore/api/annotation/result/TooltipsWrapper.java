package com.elementtimes.elementcore.api.annotation.result;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TooltipsWrapper {

    private Consumer<ItemTooltipEvent> mTooltips;
    private Predicate<ItemTooltipEvent> mCheck;

    public TooltipsWrapper(Consumer<ItemTooltipEvent> tooltips, Predicate<ItemTooltipEvent> check) {
        mTooltips = tooltips;
        mCheck = check;
    }

    public void apply(ItemTooltipEvent event) {
        if (mCheck == null || mCheck.test(event)) {
            mTooltips.accept(event);
        }
    }
}
