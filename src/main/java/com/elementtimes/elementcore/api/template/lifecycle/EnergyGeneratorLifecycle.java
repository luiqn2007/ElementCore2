package com.elementtimes.elementcore.api.template.lifecycle;

import com.elementtimes.elementcore.api.template.capability.ProxyEnergyHandler;
import com.elementtimes.elementcore.api.interfaces.block.IMachineLifecycle;
import com.elementtimes.elementcore.api.interfaces.block.ITileEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 发电机的生命周期
 * @author luqin2007
 */
public class EnergyGeneratorLifecycle implements IMachineLifecycle {

    private ITileEnergyHandler mTe;

    public EnergyGeneratorLifecycle(ITileEnergyHandler te) {
        mTe = te;
    }

    @Override
    public void onTickFinish() {
        World world = ((TileEntity) mTe).getWorld();
        if (world != null) {
            BlockPos pos = ((TileEntity) mTe).getPos();
            for (Direction value : Direction.values()) {
                ProxyEnergyHandler.Proxy proxy = mTe.getEnergyProxy(value);
                mTe.sendEnergy(proxy.getEnergyStored(), value.getOpposite(), world.getTileEntity(pos.offset(value)), proxy);
            }
        }
    }
}
