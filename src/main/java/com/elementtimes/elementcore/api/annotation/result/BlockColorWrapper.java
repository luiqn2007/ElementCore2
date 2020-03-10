package com.elementtimes.elementcore.api.annotation.result;

import net.minecraft.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockColorWrapper {

    private final Block mBlock;
    private final Object mItemColor, mBlockColor;

    public BlockColorWrapper(Block block, Object itemColor, Object blockColor) {
        mBlock = block;
        mItemColor = itemColor;
        mBlockColor = blockColor;
    }

    @OnlyIn(Dist.CLIENT)
    public net.minecraft.client.renderer.color.IItemColor getItemColor() {
        return (net.minecraft.client.renderer.color.IItemColor) mItemColor;
    }

    @OnlyIn(Dist.CLIENT)
    public net.minecraft.client.renderer.color.IBlockColor getBlockColor() {
        return (net.minecraft.client.renderer.color.IBlockColor) mBlockColor;
    }

    @OnlyIn(Dist.CLIENT)
    public void apply(net.minecraftforge.client.event.ColorHandlerEvent.Block event) {
        if ( getBlockColor() != null) {
            event.getBlockColors().register(getBlockColor(), mBlock);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void apply(net.minecraftforge.client.event.ColorHandlerEvent.Item event) {
        if (getBlockColor() != null) {
            event.getBlockColors().register(getBlockColor(), mBlock);
        }
        if (getItemColor() != null) {
            event.getItemColors().register(getItemColor(), mBlock);
        }
    }
}
