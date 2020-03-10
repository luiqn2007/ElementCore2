package com.elementtimes.elementcore.api.annotation.result;

import com.elementtimes.elementcore.api.interfaces.function.Function3;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class ScreenWrapper {

    private final Supplier<ContainerType> mType;
    private final Function3 mScreen;

    public ScreenWrapper(Supplier<ContainerType> type, Function3 screen) {
        mType = type;
        mScreen = screen;
    }

    @OnlyIn(Dist.CLIENT)
    public void apply() {
        net.minecraft.client.gui.ScreenManager.registerFactory(mType.get(),
                (net.minecraft.client.gui.ScreenManager.IScreenFactory) (container, inventory, text) ->
                        (net.minecraft.client.gui.screen.Screen) mScreen.apply(container, inventory, text));
    }

    public Supplier<ContainerType> getType() {
        return mType;
    }
}
