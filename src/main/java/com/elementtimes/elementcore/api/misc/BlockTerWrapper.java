package com.elementtimes.elementcore.api.misc;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockTerWrapper {

    public final Object ter;
    public final Class<? extends TileEntity> teClass;

    public BlockTerWrapper(Class<? extends TileEntity> teClass, Object ter) {
        if (ter instanceof net.minecraft.client.renderer.tileentity.TileEntityRenderer) {
            this.ter = ter;
        } else {
            this.ter = new net.minecraftforge.client.model.animation.TileEntityRendererAnimation();
        }
        this.teClass = teClass;
    }

    @OnlyIn(Dist.CLIENT)
    public net.minecraft.client.renderer.tileentity.TileEntityRenderer getTer() {
        return (net.minecraft.client.renderer.tileentity.TileEntityRenderer) ter;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isAnim() {
        return ter instanceof net.minecraftforge.client.model.animation.TileEntityRendererAnimation;
    }

    @OnlyIn(Dist.CLIENT)
    public void apply() {
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(teClass, getTer());
    }
}
