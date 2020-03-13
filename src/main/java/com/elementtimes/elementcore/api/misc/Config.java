package com.elementtimes.elementcore.api.misc;

import com.elementtimes.elementcore.api.ECModContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Config {
    public boolean useOBJ = false;
    public boolean useB3D = false;

    @OnlyIn(Dist.CLIENT)
    public void applyClient(ECModContainer container) {
        if (useB3D) {
            net.minecraftforge.client.model.obj.OBJLoader.INSTANCE.addDomain(container.id());
        }
        if (useB3D) {
            net.minecraftforge.client.model.b3d.B3DLoader.INSTANCE.addDomain(container.id());
        }
    }
}
