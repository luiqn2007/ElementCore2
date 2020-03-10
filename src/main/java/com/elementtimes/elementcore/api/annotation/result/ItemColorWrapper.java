package com.elementtimes.elementcore.api.annotation.result;

import net.minecraft.util.IItemProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemColorWrapper {

    private final IItemProvider mItem;
    private final Object mItemColor;

    public ItemColorWrapper(IItemProvider item, Object itemColor) {
        mItem = item;
        mItemColor = itemColor;
    }

    @OnlyIn(Dist.CLIENT)
    public net.minecraft.client.renderer.color.IItemColor getItemColor() {
        return (net.minecraft.client.renderer.color.IItemColor) mItemColor;
    }

    @OnlyIn(Dist.CLIENT)
    public void apply(net.minecraftforge.client.event.ColorHandlerEvent.Item event) {
        if (getItemColor() != null) {
            event.getItemColors().register(getItemColor(), mItem);
        }
    }
}
