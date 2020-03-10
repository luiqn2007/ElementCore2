package com.elementtimes.elementcore.api.event;

import com.elementtimes.elementcore.api.ECModContainer;
import com.elementtimes.elementcore.api.ECModElements;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author luqin2007
 */
@OnlyIn(Dist.CLIENT)
public class ClientEvent {

    private ECModContainer mContainer;

    public ClientEvent(ECModContainer container) {
        mContainer = container;
    }

    private ECModElements elements() {
        return mContainer.elements();
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        elements().keys.forEach(k -> k.testPress(event));
    }

    @SubscribeEvent
    public void onColor(ColorHandlerEvent.Item event) {
        elements().blockColors.forEach(c -> c.apply(event));
        elements().itemColors.forEach(c -> c.apply(event));
    }

    @SubscribeEvent
    public void onColor(ColorHandlerEvent.Block event) {
        elements().blockColors.forEach(c -> c.apply(event));
    }
}
