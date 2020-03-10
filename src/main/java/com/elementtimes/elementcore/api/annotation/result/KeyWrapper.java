package com.elementtimes.elementcore.api.annotation.result;

import com.elementtimes.elementcore.api.interfaces.invoker.VoidInvoker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KeyWrapper {

    private Object mKey;
    private VoidInvoker mListener;

    public KeyWrapper(Object key, VoidInvoker listener) {
        mKey = key;
        mListener = listener;
    }

    @OnlyIn(Dist.CLIENT)
    public net.minecraft.client.settings.KeyBinding getKey() {
        return (net.minecraft.client.settings.KeyBinding) mKey;
    }

    @OnlyIn(Dist.CLIENT)
    public void testPress(net.minecraftforge.client.event.InputEvent.KeyInputEvent event) {
        if (getKey().isPressed()) {
            mListener.invoke(event, getKey());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void apply() {
        net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(getKey());
    }
}
