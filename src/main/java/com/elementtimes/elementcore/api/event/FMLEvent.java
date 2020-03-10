package com.elementtimes.elementcore.api.event;

import com.elementtimes.elementcore.api.ECModContainer;
import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.result.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class FMLEvent {

    private ECModContainer mContainer;

    public FMLEvent(ECModContainer container) {
        mContainer = container;
    }

    private ECModElements elements() {
        return mContainer.elements();
    }

    public void apply(IEventBus eventBus) {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    // some preinit code
    private void setup(final FMLCommonSetupEvent event) {
        ECModElements elements = elements();
        elements.features.forEach(FeatureWrapper::apply);
        elements.capabilities.forEach(CapabilityData::apply);
        elements.entitySpawns.forEach(EntitySpawner::apply);
        elements.netSimples.forEach(a -> a.apply(elements));
        elements.netEvents.forEach(elements.eventChannel::registerObject);
    }

    // do something that can only be done on the client
    private void doClientStuff(final FMLClientSetupEvent event) {
        ECModElements elements = elements();
        elements.config.applyClient(mContainer);
        elements.ters.forEach(TerWrapper::apply);
        elements.entityRenders.forEach(EntityRendererWrapper::apply);
        elements.containerScreens.forEach(ScreenWrapper::apply);
        elements.keys.forEach(KeyWrapper::apply);
    }

    // do something that can only be done on the server
    private void doServerStuff(final FMLDedicatedServerSetupEvent event) { }

    // some example code to dispatch IMC to another mod
    private void enqueueIMC(final InterModEnqueueEvent event) { }

    // some example code to receive and process InterModComms from other mods
    private void processIMC(final InterModProcessEvent event) { }
}
