package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.ModTer;
import com.elementtimes.elementcore.api.annotation.result.TerWrapper;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.elementtimes.elementcore.api.helper.RefHelper;
import com.elementtimes.elementcore.api.utils.CommonUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

public class TileEntityLoader {

    public static void load(ECModElements elements) {
        loadTileEntityType(elements);
        if (CommonUtils.isClient()) {
            loadTer(elements);
        }
    }

    private static void loadTileEntityType(ECModElements elements) {
    }

    @OnlyIn(Dist.CLIENT)
    private static void loadTer(ECModElements elements) {
        ObjHelper.stream(elements, ModTer.class).forEach(data -> {
            ObjHelper.findClass(elements, data.getClassType()).filter(TileEntity.class::isAssignableFrom).ifPresent(teClass -> {
                Class<?> terClass = net.minecraft.client.renderer.tileentity.TileEntityRenderer.class;
                Optional<?> optional = RefHelper.get(elements, ObjHelper.getDefault(data), terClass);
                elements.ters.add(new TerWrapper((Class<? extends TileEntity>) teClass, optional.orElse(null)));
            });
        });
    }
}
